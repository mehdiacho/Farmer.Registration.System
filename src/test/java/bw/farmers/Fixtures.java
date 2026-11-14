package bw.farmers;

import bw.farmers.model.Farmer;
import bw.farmers.model.Gender;
import bw.farmers.model.Status;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/** Fictional test data. */
public final class Fixtures {

    public static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-14T08:00:00Z"), ZoneOffset.UTC);

    private Fixtures() {
    }

    public static Farmer farmer(String id, String email, String contact) {
        return new Farmer(id, "Naledi", "Sebina", contact, LocalDate.of(1990, 5, 17), Gender.FEMALE,
                "PO Box 101, Gaborone", "South-East", "Gaborone", email, 120, 80, Status.PENDING);
    }

    public static Farmer farmer() {
        return farmer("123456789", "naledi@example.com", "71234567");
    }

    public static char[] pw() {
        return "fields-of-sorghum".toCharArray();
    }
}
