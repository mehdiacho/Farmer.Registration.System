package bw.farmers.db;

import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Reads and writes farmers. Every query is parameterised. */
public final class FarmerRepository {

    private static final String COLUMNS = "id_number, first_name, last_name, contact, date_of_birth, gender, "
            + "postal_address, district, sub_district, email, farm_size, farming_area, status";

    private final Database db;

    public FarmerRepository(Database db) {
        this.db = db;
    }

    /** Inserts {@code f}, optionally linked to the login account {@code userId}. */
    public void insert(Farmer f, Long userId) {
        db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "INSERT INTO farmers (" + COLUMNS + ", user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                bind(ps, f);
                if (userId == null) {
                    ps.setNull(14, Types.INTEGER);
                } else {
                    ps.setLong(14, userId);
                }
                ps.executeUpdate();
            }
            return null;
        });
    }

    /**
     * Replaces the farmer whose ID number was {@code originalId} with {@code f}
     * (the ID number itself may change).
     */
    public boolean update(String originalId, Farmer f) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "UPDATE farmers SET id_number=?, first_name=?, last_name=?, contact=?, date_of_birth=?, gender=?, "
                            + "postal_address=?, district=?, sub_district=?, email=?, farm_size=?, farming_area=?, "
                            + "status=?, updated_at=CURRENT_TIMESTAMP WHERE id_number=?")) {
                bind(ps, f);
                ps.setString(14, originalId);
                return ps.executeUpdate() == 1;
            }
        });
    }

    public boolean setStatus(String idNumber, Status status) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "UPDATE farmers SET status=?, updated_at=CURRENT_TIMESTAMP WHERE id_number=?")) {
                ps.setString(1, status.name());
                ps.setString(2, idNumber);
                return ps.executeUpdate() == 1;
            }
        });
    }

    /** Deletes the farmer and, if they have one, their (non-admin) login account. */
    public boolean delete(String idNumber) {
        return db.inTransaction(() -> {
            Long userId;
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "SELECT user_id FROM farmers WHERE id_number=?")) {
                ps.setString(1, idNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    long id = rs.getLong(1);
                    userId = rs.wasNull() ? null : id;
                }
            }
            try (PreparedStatement ps = db.connection().prepareStatement("DELETE FROM farmers WHERE id_number=?")) {
                ps.setString(1, idNumber);
                ps.executeUpdate();
            }
            if (userId != null) {
                try (PreparedStatement ps = db.connection().prepareStatement(
                        "DELETE FROM users WHERE id=? AND is_admin=0")) {
                    ps.setLong(1, userId);
                    ps.executeUpdate();
                }
            }
            return true;
        });
    }

    public Optional<Farmer> findById(String idNumber) {
        return queryOne("SELECT " + COLUMNS + " FROM farmers WHERE id_number=?", idNumber);
    }

    /** The login account linked to this farmer, if any. */
    public Optional<Long> userIdOf(String idNumber) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "SELECT user_id FROM farmers WHERE id_number=? AND user_id IS NOT NULL")) {
                ps.setString(1, idNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(rs.getLong(1)) : Optional.empty();
                }
            }
        });
    }

    public Optional<Farmer> findByUserId(long userId) {
        return queryOne("SELECT " + COLUMNS + " FROM farmers WHERE user_id=?", userId);
    }

    /** Farmers matching {@code filter}, sorted by district, sub-district, then name. */
    public List<Farmer> find(ReportFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM farmers WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (filter.district() != null) {
            sql.append(" AND district=?");
            args.add(filter.district());
        }
        if (filter.subDistrict() != null) {
            sql.append(" AND sub_district=?");
            args.add(filter.subDistrict());
        }
        if (filter.gender() != null) {
            sql.append(" AND gender=?");
            args.add(filter.gender().name());
        }
        if (filter.status() != null) {
            sql.append(" AND status=?");
            args.add(filter.status().name());
        }
        sql.append(" ORDER BY district, sub_district, last_name, first_name");
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(sql.toString())) {
                for (int i = 0; i < args.size(); i++) {
                    ps.setObject(i + 1, args.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    List<Farmer> out = new ArrayList<>();
                    while (rs.next()) {
                        out.add(read(rs));
                    }
                    return out;
                }
            }
        });
    }

    public boolean idNumberTaken(String idNumber, String exceptId) {
        return exists("id_number", idNumber, exceptId);
    }

    public boolean emailTaken(String email, String exceptId) {
        return exists("email", email, exceptId);
    }

    public boolean contactTaken(String contact, String exceptId) {
        return exists("contact", contact, exceptId);
    }

    // column is always one of the fixed names above, never user input.
    private boolean exists(String column, String value, String exceptId) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(
                    "SELECT 1 FROM farmers WHERE " + column + "=? AND id_number IS NOT ?")) {
                ps.setString(1, value);
                ps.setString(2, exceptId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    private Optional<Farmer> queryOne(String sql, Object arg) {
        return db.inTransaction(() -> {
            try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
                ps.setObject(1, arg);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(read(rs)) : Optional.empty();
                }
            }
        });
    }

    private static void bind(PreparedStatement ps, Farmer f) throws SQLException {
        ps.setString(1, f.idNumber());
        ps.setString(2, f.firstName());
        ps.setString(3, f.lastName());
        ps.setString(4, f.contact());
        ps.setString(5, f.dateOfBirth().toString());
        ps.setString(6, f.gender().name());
        ps.setString(7, f.postalAddress());
        ps.setString(8, f.district());
        ps.setString(9, f.subDistrict());
        ps.setString(10, f.email());
        ps.setDouble(11, f.farmSize());
        ps.setDouble(12, f.farmingArea());
        ps.setString(13, f.status().name());
    }

    private static Farmer read(ResultSet rs) throws SQLException {
        return new Farmer(
                rs.getString("id_number"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("contact"),
                LocalDate.parse(rs.getString("date_of_birth")),
                Gender.valueOf(rs.getString("gender")),
                rs.getString("postal_address"),
                rs.getString("district"),
                rs.getString("sub_district"),
                rs.getString("email"),
                rs.getDouble("farm_size"),
                rs.getDouble("farming_area"),
                Status.valueOf(rs.getString("status")));
    }
}
