package bw.farmers.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The embedded SQLite database. One file, created and migrated on first open, so the app
 * runs from a clean clone with no server to install.
 */
public final class Database implements AutoCloseable {

    static final int SCHEMA_VERSION = 1;

    private final Connection connection;

    private Database(Connection connection) {
        this.connection = connection;
    }

    /** Opens (creating if needed) the database file at {@code file}. */
    public static Database open(Path file) {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            return connect("jdbc:sqlite:" + file.toAbsolutePath());
        } catch (IOException e) {
            throw new DataException("Cannot create the folder for " + file, e);
        }
    }

    /** A private in-memory database, used by tests. */
    public static Database inMemory() {
        return connect("jdbc:sqlite::memory:");
    }

    private static Database connect(String url) {
        try {
            Connection c = DriverManager.getConnection(url);
            try (Statement s = c.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");
                s.execute("PRAGMA journal_mode = WAL");
            }
            Database db = new Database(c);
            db.migrate();
            return db;
        } catch (SQLException e) {
            throw new DataException("Cannot open database " + url, e);
        }
    }

    Connection connection() {
        return connection;
    }

    private void migrate() throws SQLException {
        int version;
        try (Statement s = connection.createStatement();
             var rs = s.executeQuery("PRAGMA user_version")) {
            version = rs.next() ? rs.getInt(1) : 0;
        }
        if (version >= SCHEMA_VERSION) {
            return;
        }
        inTransaction(() -> {
            try (Statement s = connection.createStatement()) {
                for (String sql : loadSchema().split(";")) {
                    String stmt = stripComments(sql).strip();
                    if (!stmt.isEmpty()) {
                        s.execute(stmt);
                    }
                }
                s.execute("PRAGMA user_version = " + SCHEMA_VERSION);
            }
            return null;
        });
    }

    private static String stripComments(String sql) {
        StringBuilder out = new StringBuilder();
        for (String line : sql.split("\n")) {
            if (!line.strip().startsWith("--")) {
                out.append(line).append('\n');
            }
        }
        return out.toString();
    }

    private static String loadSchema() {
        try (InputStream in = Database.class.getResourceAsStream("schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("schema.sql missing from the classpath");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataException("Cannot read schema.sql", e);
        }
    }

    /** Work that runs inside a transaction and may throw {@link SQLException}. */
    @FunctionalInterface
    public interface SqlWork<T> {
        T run() throws SQLException;
    }

    /** Runs {@code work} in one transaction: everything is committed, or nothing is. */
    public <T> T inTransaction(SqlWork<T> work) {
        try {
            boolean outer = connection.getAutoCommit();
            if (!outer) {
                return work.run(); // already inside a transaction
            }
            connection.setAutoCommit(false);
            try {
                T result = work.run();
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DataException("Cannot close database", e);
        }
    }
}
