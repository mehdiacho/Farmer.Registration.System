package bw.farmers.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher(1_000);

    @Test
    void verifiesTheRightPasswordOnly() {
        String stored = hasher.hash("correct horse".toCharArray());
        assertTrue(hasher.verify("correct horse".toCharArray(), stored));
        assertFalse(hasher.verify("correct hors".toCharArray(), stored));
    }

    @Test
    void neverStoresThePasswordAndSaltsEveryHash() {
        String a = hasher.hash("password123".toCharArray());
        String b = hasher.hash("password123".toCharArray());
        assertFalse(a.contains("password123"));
        assertNotEquals(a, b, "two hashes of one password must differ (random salt)");
        assertTrue(a.startsWith("pbkdf2-sha256$1000$"));
    }

    @Test
    void rejectsMalformedOrLegacyValues() {
        assertFalse(hasher.verify("x".toCharArray(), null));
        assertFalse(hasher.verify("x".toCharArray(), ""));
        assertFalse(hasher.verify("x".toCharArray(), "pbkdf2-sha256$abc$$"));
        // The 2022 app stored Arrays.toString(password) in plain text; such rows must never match.
        assertFalse(hasher.verify("demo".toCharArray(), "[d, e, m, o]"));
    }

    @Test
    void wipeClearsTheBuffer() {
        char[] pw = "secret".toCharArray();
        PasswordHasher.wipe(pw);
        assertTrue(new String(pw).chars().allMatch(c -> c == 0));
    }
}
