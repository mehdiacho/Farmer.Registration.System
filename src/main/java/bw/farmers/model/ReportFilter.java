package bw.farmers.model;

/**
 * Which farmers a list or report should include. A {@code null} field means "any".
 */
public record ReportFilter(String district, String subDistrict, Gender gender, Status status) {

    public static final ReportFilter ALL = new ReportFilter(null, null, null, null);

    public boolean matches(Farmer f) {
        return (district == null || district.equals(f.district()))
                && (subDistrict == null || subDistrict.equals(f.subDistrict()))
                && (gender == null || gender == f.gender())
                && (status == null || status == f.status());
    }

    /** A short human description, e.g. "District: Central, Gender: Female". */
    public String describe() {
        StringBuilder sb = new StringBuilder();
        append(sb, "District", district);
        append(sb, "Sub-district", subDistrict);
        append(sb, "Gender", gender == null ? null : gender.label());
        append(sb, "Status", status == null ? null : status.label());
        return sb.isEmpty() ? "All farmers" : sb.toString();
    }

    private static void append(StringBuilder sb, String name, String value) {
        if (value == null) {
            return;
        }
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
        sb.append(name).append(": ").append(value);
    }
}
