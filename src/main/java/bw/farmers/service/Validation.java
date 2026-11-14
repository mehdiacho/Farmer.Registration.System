package bw.farmers.service;

import bw.farmers.model.Districts;
import bw.farmers.model.Farmer;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Field rules for the registration form. Returns every problem at once, in form order. */
public final class Validation {

    /** Omang numbers are 9 digits. */
    private static final Pattern ID_NUMBER = Pattern.compile("\\d{9}");
    /** Botswana numbers are 8 digits; mobiles start with 7, landlines with 2-6. */
    private static final Pattern CONTACT = Pattern.compile("[2-7]\\d{7}");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public static final int MIN_AGE = 16;
    public static final int MIN_PASSWORD_LENGTH = 8;

    private Validation() {
    }

    public static List<String> farmer(Farmer f, LocalDate today) {
        List<String> errors = new ArrayList<>();
        if (f.idNumber() == null || !ID_NUMBER.matcher(f.idNumber()).matches()) {
            errors.add("ID number must be 9 digits.");
        }
        if (blank(f.firstName())) {
            errors.add("First name is required.");
        }
        if (blank(f.lastName())) {
            errors.add("Last name is required.");
        }
        if (f.contact() == null || !CONTACT.matcher(f.contact()).matches()) {
            errors.add("Contact number must be 8 digits, e.g. 71234567.");
        }
        if (f.dateOfBirth() == null) {
            errors.add("Date of birth is required.");
        } else if (f.dateOfBirth().isAfter(today) || Period.between(f.dateOfBirth(), today).getYears() < MIN_AGE) {
            errors.add("Farmers must be at least " + MIN_AGE + " years old.");
        }
        if (f.gender() == null) {
            errors.add("Gender is required.");
        }
        if (blank(f.postalAddress())) {
            errors.add("Postal address is required.");
        }
        if (!Districts.isValid(f.district(), f.subDistrict())) {
            errors.add("Choose a district and one of its sub-districts.");
        }
        if (!isEmail(f.email())) {
            errors.add("Enter a valid email address.");
        }
        if (!(f.farmSize() > 0)) {
            errors.add("Farm size must be more than 0 hectares.");
        }
        if (f.farmingArea() < 0 || Double.isNaN(f.farmingArea())) {
            errors.add("Farming area cannot be negative.");
        } else if (f.farmingArea() > f.farmSize()) {
            errors.add("Farming area cannot be larger than the farm.");
        }
        return errors;
    }

    public static List<String> password(char[] password, char[] confirm) {
        List<String> errors = new ArrayList<>();
        if (password == null || password.length < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        } else if (confirm == null || !java.util.Arrays.equals(password, confirm)) {
            errors.add("Passwords do not match.");
        }
        return errors;
    }

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
