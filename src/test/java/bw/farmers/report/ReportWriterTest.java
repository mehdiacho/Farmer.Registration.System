package bw.farmers.report;

import bw.farmers.Fixtures;
import bw.farmers.demo.DemoData;
import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportWriterTest {

    private static final LocalDateTime AT = LocalDateTime.of(2026, 11, 14, 10, 30);

    @Test
    void textReportHasTitleFilterTableAndTotals() {
        String txt = ReportWriter.text(List.of(Fixtures.farmer()),
                new ReportFilter("South-East", null, Gender.FEMALE, null), AT);
        String[] lines = txt.split("\\R");
        assertEquals("FarmersBW - Registered farmers report", lines[0]);
        assertEquals("Filter:    District: South-East, Gender: Female", lines[1]);
        assertEquals("Generated: 14 Nov 2026, 10:30", lines[2]);
        assertTrue(lines[4].startsWith("ID number  Name"), lines[4]);
        assertTrue(lines[6].startsWith("123456789  Naledi Sebina"), lines[6]);
        assertEquals("1 farmer, 120 ha of farmland, 80 ha available for farming.", lines[8]);
    }

    @Test
    void columnsLineUp() {
        List<Farmer> many = DemoData.generate(12, 7);
        String[] lines = ReportWriter.text(many, ReportFilter.ALL, AT).split("\\R");
        int statusAt = lines[4].indexOf("Status");
        for (int i = 6; i < 6 + many.size(); i++) {
            String status = lines[i].substring(statusAt).strip();
            assertTrue(List.of("Pending", "Approved", "Declined").contains(status), lines[i]);
        }
    }

    @Test
    void csvQuotesAndDefusesFormulas() {
        Farmer f = Fixtures.farmer();
        Farmer tricky = new Farmer(f.idNumber(), "=HYPERLINK(\"x\")", "O\"Neil, Jr", f.contact(), f.dateOfBirth(),
                f.gender(), f.postalAddress(), f.district(), f.subDistrict(), f.email(), 1.5, 1, Status.APPROVED);
        String csv = ReportWriter.csv(List.of(tricky));
        String row = csv.split("\r\n")[1];
        assertTrue(row.startsWith("123456789,\"'=HYPERLINK(\"\"x\"\") O\"\"Neil, Jr\""), row);
        assertTrue(row.endsWith(",1.5,1,Approved"), row);
    }

    @Test
    void writesByExtension(@TempDir Path dir) throws Exception {
        ReportWriter.write(dir.resolve("r.txt"), List.of(Fixtures.farmer()), ReportFilter.ALL, AT);
        ReportWriter.write(dir.resolve("r.CSV"), List.of(Fixtures.farmer()), ReportFilter.ALL, AT);
        assertTrue(Files.readString(dir.resolve("r.txt")).startsWith("FarmersBW"));
        assertTrue(Files.readString(dir.resolve("r.CSV")).startsWith("ID number,Name"));
    }
}
