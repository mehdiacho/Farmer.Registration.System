package bw.farmers.report;

import bw.farmers.model.Farmer;
import bw.farmers.model.ReportFilter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Writes a list of farmers as a report: an aligned plain-text table (the assignment asked for
 * a .txt file) or CSV, chosen by the file extension.
 */
public final class ReportWriter {

    private record Column(String title, Function<Farmer, String> value, boolean right) {
    }

    private static final List<Column> COLUMNS = List.of(
            new Column("ID number", Farmer::idNumber, false),
            new Column("Name", Farmer::fullName, false),
            new Column("Gender", f -> f.gender().label(), false),
            new Column("Contact", Farmer::contact, false),
            new Column("Email", Farmer::email, false),
            new Column("District", Farmer::district, false),
            new Column("Sub-district", Farmer::subDistrict, false),
            new Column("Farm (ha)", f -> hectares(f.farmSize()), true),
            new Column("Farming (ha)", f -> hectares(f.farmingArea()), true),
            new Column("Status", f -> f.status().label(), false));

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH);

    private ReportWriter() {
    }

    public static void write(Path file, List<Farmer> farmers, ReportFilter filter, LocalDateTime generated)
            throws IOException {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        String body = name.endsWith(".csv") ? csv(farmers) : text(farmers, filter, generated);
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write(body);
        }
    }

    /** The plain-text report: a title block, an aligned table and totals. */
    public static String text(List<Farmer> farmers, ReportFilter filter, LocalDateTime generated) {
        List<String[]> rows = new ArrayList<>();
        for (Farmer f : farmers) {
            rows.add(COLUMNS.stream().map(c -> c.value().apply(f)).toArray(String[]::new));
        }
        int[] width = new int[COLUMNS.size()];
        for (int i = 0; i < width.length; i++) {
            width[i] = COLUMNS.get(i).title().length();
            for (String[] r : rows) {
                width[i] = Math.max(width[i], r[i].length());
            }
        }
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("FarmersBW - Registered farmers report").append(nl);
        sb.append("Filter:    ").append(filter.describe()).append(nl);
        sb.append("Generated: ").append(STAMP.format(generated)).append(nl).append(nl);
        appendRow(sb, COLUMNS.stream().map(Column::title).toArray(String[]::new), width, nl);
        StringBuilder rule = new StringBuilder();
        for (int i = 0; i < width.length; i++) {
            rule.append(i == 0 ? "" : "  ").append("-".repeat(width[i]));
        }
        sb.append(rule).append(nl);
        for (String[] r : rows) {
            appendRow(sb, r, width, nl);
        }
        double size = farmers.stream().mapToDouble(Farmer::farmSize).sum();
        double area = farmers.stream().mapToDouble(Farmer::farmingArea).sum();
        sb.append(nl).append(farmers.size()).append(farmers.size() == 1 ? " farmer" : " farmers")
                .append(", ").append(hectares(size)).append(" ha of farmland, ")
                .append(hectares(area)).append(" ha available for farming.").append(nl);
        return sb.toString();
    }

    private static void appendRow(StringBuilder sb, String[] cells, int[] width, String nl) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                line.append("  ");
            }
            String fmt = "%" + (COLUMNS.get(i).right() ? "" : "-") + width[i] + "s";
            line.append(String.format(fmt, cells[i]));
        }
        sb.append(line.toString().stripTrailing()).append(nl);
    }

    /** RFC 4180 CSV with a header row. */
    public static String csv(List<Farmer> farmers) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", COLUMNS.stream().map(c -> quote(c.title())).toList())).append("\r\n");
        for (Farmer f : farmers) {
            sb.append(String.join(",", COLUMNS.stream().map(c -> quote(c.value().apply(f))).toList())).append("\r\n");
        }
        return sb.toString();
    }

    private static String quote(String s) {
        boolean needs = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")
                || s.startsWith("=") || s.startsWith("+") || s.startsWith("-") || s.startsWith("@");
        if (!needs) {
            return s;
        }
        // A leading quote stops spreadsheet apps treating a cell as a formula.
        String safe = s.matches("^[=+\\-@].*") ? "'" + s : s;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    static String hectares(double v) {
        return v == Math.rint(v) ? String.format(Locale.ROOT, "%,.0f", v) : String.format(Locale.ROOT, "%,.1f", v);
    }
}
