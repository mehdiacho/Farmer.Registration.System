package bw.farmers.db;

import bw.farmers.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/** Login accounts. Stores password hashes only, never passwords. */
public final class UserRepository {

    /** An account together with its stored password hash, for checking a login. */
    public record Credentials(User user, String passwordHash) {
    }

    private final Database db;

    public UserRepository(Database db) {
        this.db = db;
    }

    public long insert(String email, String displayName, String passwordHash, boolean admin) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "INSERT INTO users (email, display_name, password_hash, is_admin) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, email);
                ps.setString(2, displayName);
                ps.setString(3, passwordHash);
                ps.setInt(4, admin ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return keys.getLong(1);
                }
            }
        });
    }

    public Optional<Credentials> findByEmail(String email) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "SELECT id, email, display_name, is_admin, password_hash FROM users WHERE email=?")) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(new Credentials(read(rs), rs.getString("password_hash")));
                }
            }
        });
    }

    public Optional<Credentials> findById(long id) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "SELECT id, email, display_name, is_admin, password_hash FROM users WHERE id=?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(new Credentials(read(rs), rs.getString("password_hash")));
                }
            }
        });
    }

    public boolean emailTaken(String email, Long exceptUserId) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "SELECT 1 FROM users WHERE email=? AND id IS NOT ?")) {
                ps.setString(1, email);
                ps.setObject(2, exceptUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    public void updateProfile(long userId, String email, String displayName) {
        db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "UPDATE users SET email=?, display_name=? WHERE id=?")) {
                ps.setString(1, email);
                ps.setString(2, displayName);
                ps.setLong(3, userId);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public void updatePasswordHash(long userId, String passwordHash) {
        db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "UPDATE users SET password_hash=? WHERE id=?")) {
                ps.setString(1, passwordHash);
                ps.setLong(2, userId);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public boolean anyAdmin() {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement("SELECT 1 FROM users WHERE is_admin=1");
                 ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        });
    }

    private static User read(ResultSet rs) throws SQLException {
        return new User(rs.getLong("id"), rs.getString("email"), rs.getString("display_name"),
                rs.getInt("is_admin") == 1);
    }
}
