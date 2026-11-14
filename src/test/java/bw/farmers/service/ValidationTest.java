package bw.farmers.service;

import bw.farmers.Fixtures;
import bw.farmers.model.Farmer;
import bw.farmers.model.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 14);

    @Test
    void aCompleteFormHasNoProblems() {
        assertEquals(List.of(), Validation.farmer(Fixtures.farmer(), TODAY));
    }

    @Test
    void reportsEveryBadFieldAtOnce() {
        Farmer bad = new Farmer("12345", " ", "", "1234", TODAY.minusYears(5), null, "", "Central", "Gaborone",
                "not-an-email", 0, -1, Status.PENDING);
        List<String> problems = Validation.farmer(bad, TODAY);
        assertEquals(11, problems.size(), problems.toString());
    }

    @Test
    void farmingAreaCannotExceedTheFarm() {
        Farmer f = Fixtures.farmer();
        Farmer tooBig = new Farmer(f.idNumber(), f.firstName(), f.lastName(), f.contact(), f.dateOfBirth(),
                f.gender(), f.postalAddress(), f.district(), f.subDistrict(), f.email(), 10, 11, f.status());
        assertEquals(List.of("Farming area cannot be larger than the farm."), Validation.farmer(tooBig, TODAY));
    }

    @Test
    void unreadableNumbersFail() {
        Farmer f = Fixtures.farmer();
        Farmer nan = new Farmer(f.idNumber(), f.firstName(), f.lastName(), f.contact(), f.dateOfBirth(),
                f.gender(), f.postalAddress(), f.district(), f.subDistrict(), f.email(), Double.NaN, Double.NaN,
                f.status());
        assertEquals(2, Validation.farmer(nan, TODAY).size());
    }

    @Test
    void passwordRules() {
        assertTrue(Validation.password("long enough".toCharArray(), "long enough".toCharArray()).isEmpty());
        assertEquals(List.of("Password must be at least 8 characters."),
                Validation.password("short".toCharArray(), "short".toCharArray()));
        assertEquals(List.of("Passwords do not match."),
                Validation.password("long enough".toCharArray(), "long enough!".toCharArray()));
    }
}
