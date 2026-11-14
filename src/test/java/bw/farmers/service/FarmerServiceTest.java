package bw.farmers.service;

import bw.farmers.Fixtures;
import bw.farmers.auth.PasswordHasher;
import bw.farmers.db.Database;
import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.ReportFilter;
import bw.farmers.model.Status;
import bw.farmers.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FarmerServiceTest {

    private Database db;
    private FarmerService service;

    @BeforeEach
    void setUp() {
        db = Database.inMemory();
        service = new FarmerService(db, new PasswordHasher(1_000), Fixtures.CLOCK);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void firstRunNeedsAnAdmin() throws Exception {
        assertTrue(service.needsAdminSetup());
        User admin = service.createAdmin("Ministry Admin", "admin@example.com", Fixtures.pw(), Fixtures.pw());
        assertTrue(admin.admin());
        assertFalse(service.needsAdminSetup());
        assertEquals(admin, service.signIn("ADMIN@example.com ", Fixtures.pw()).orElseThrow());
    }

    @Test
    void signUpThenSignInAsAPendingFarmer() throws Exception {
        User u = service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        assertFalse(u.admin());
        assertEquals("Naledi Sebina", u.displayName());

        assertTrue(service.signIn("naledi@example.com", "wrong password".toCharArray()).isEmpty());
        User signedIn = service.signIn("naledi@example.com", Fixtures.pw()).orElseThrow();
        assertEquals(Status.PENDING, service.profileOf(signedIn).orElseThrow().status());
    }

    @Test
    void duplicatesAreRejectedWithClearMessages() throws Exception {
        service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        ValidationException e = assertThrows(ValidationException.class,
                () -> service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw()));
        assertEquals(List.of("That ID number is already registered.", "That email address is already registered.",
                "That contact number is already registered."), e.problems());
    }

    @Test
    void cannotRegisterWithAnAdminsEmail() throws Exception {
        service.createAdmin("Admin", "naledi@example.com", Fixtures.pw(), Fixtures.pw());
        ValidationException e = assertThrows(ValidationException.class,
                () -> service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw()));
        assertEquals(List.of("That email address is already registered."), e.problems());
    }

    @Test
    void adminDecisionsAndADeclinedFarmerResubmitting() throws Exception {
        User u = service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        service.setStatus("123456789", Status.DECLINED);

        Farmer fixed = service.profileOf(u).orElseThrow();
        Farmer saved = service.updateOwnProfile(u, new Farmer(fixed.idNumber(), fixed.firstName(), "Sebina-Molefe",
                fixed.contact(), fixed.dateOfBirth(), fixed.gender(), fixed.postalAddress(), fixed.district(),
                fixed.subDistrict(), fixed.email(), fixed.farmSize(), fixed.farmingArea(), fixed.status()));
        assertEquals(Status.PENDING, saved.status(), "editing a declined registration sends it back for review");

        service.setStatus("123456789", Status.APPROVED);
        assertEquals(Status.APPROVED, service.profileOf(u).orElseThrow().status());
    }

    @Test
    void farmerChangingEmailKeepsTheirLogin() throws Exception {
        User u = service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        Farmer f = service.profileOf(u).orElseThrow();
        service.updateOwnProfile(u, new Farmer(f.idNumber(), f.firstName(), f.lastName(), f.contact(),
                f.dateOfBirth(), f.gender(), f.postalAddress(), f.district(), f.subDistrict(), "new@example.com",
                f.farmSize(), f.farmingArea(), f.status()));
        assertTrue(service.signIn("naledi@example.com", Fixtures.pw()).isEmpty());
        assertTrue(service.signIn("new@example.com", Fixtures.pw()).isPresent());
    }

    @Test
    void adminEditCanChangeTheIdNumberAndEmail() throws Exception {
        service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        Farmer f = service.list(ReportFilter.ALL).get(0);
        service.adminUpdate(f.idNumber(), new Farmer("987654321", f.firstName(), f.lastName(), f.contact(),
                f.dateOfBirth(), f.gender(), f.postalAddress(), f.district(), f.subDistrict(), "moved@example.com",
                f.farmSize(), f.farmingArea(), Status.APPROVED));
        Farmer after = service.list(ReportFilter.ALL).get(0);
        assertEquals("987654321", after.idNumber());
        assertEquals(Status.APPROVED, after.status());
        assertTrue(service.signIn("moved@example.com", Fixtures.pw()).isPresent());
    }

    @Test
    void deletingAFarmerAlsoRemovesTheirLogin() throws Exception {
        service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        assertTrue(service.delete("123456789"));
        assertTrue(service.list(ReportFilter.ALL).isEmpty());
        assertTrue(service.signIn("naledi@example.com", Fixtures.pw()).isEmpty());
        assertFalse(service.delete("123456789"));
    }

    @Test
    void changePasswordChecksTheCurrentOne() throws Exception {
        User u = service.register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        char[] next = "a brand new password".toCharArray();
        assertThrows(ValidationException.class,
                () -> service.changePassword(u, "nope".toCharArray(), next, next));
        service.changePassword(u, Fixtures.pw(), next, next);
        assertTrue(service.signIn("naledi@example.com", next).isPresent());
    }

    @Test
    void filtersByDistrictSubDistrictGenderAndStatus() throws Exception {
        service.register(Fixtures.farmer("111111111", "a@example.com", "71111111"), Fixtures.pw(), Fixtures.pw());
        Farmer b = Fixtures.farmer("222222222", "b@example.com", "72222222");
        service.register(new Farmer(b.idNumber(), "Thabo", "Kgosi", b.contact(), b.dateOfBirth(), Gender.MALE,
                b.postalAddress(), "Central", "Tutume", b.email(), 60, 55, Status.PENDING), Fixtures.pw(), Fixtures.pw());
        service.setStatus("222222222", Status.APPROVED);

        assertEquals(2, service.list(ReportFilter.ALL).size());
        assertEquals(1, service.list(new ReportFilter("Central", null, null, null)).size());
        assertEquals(1, service.list(new ReportFilter("South-East", "Gaborone", null, null)).size());
        assertEquals(0, service.list(new ReportFilter("South-East", "Otse", null, null)).size());
        assertEquals("Thabo", service.list(new ReportFilter(null, null, Gender.MALE, null)).get(0).firstName());
        assertEquals("Naledi", service.list(new ReportFilter(null, null, null, Status.PENDING)).get(0).firstName());
    }

    @Test
    void dataSurvivesReopeningTheFile(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("nested/farmers.db");
        try (Database first = Database.open(file)) {
            new FarmerService(first, new PasswordHasher(1_000), Fixtures.CLOCK)
                    .register(Fixtures.farmer(), Fixtures.pw(), Fixtures.pw());
        }
        try (Database second = Database.open(file)) {
            assertEquals(1, new FarmerService(second).list(ReportFilter.ALL).size());
        }
    }
}
