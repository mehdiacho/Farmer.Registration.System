package bw.farmers.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A registered farmer, as the Ministry of Agriculture captures them.
 *
 * @param idNumber      Omang (national ID) number, 9 digits
 * @param contact       8-digit Botswana phone number
 * @param farmSize      size of the farm in hectares
 * @param farmingArea   area available for farming (debushed) in hectares
 */
public record Farmer(
        String idNumber,
        String firstName,
        String lastName,
        String contact,
        LocalDate dateOfBirth,
        Gender gender,
        String postalAddress,
        String district,
        String subDistrict,
        String email,
        double farmSize,
        double farmingArea,
        Status status) {

    public Farmer {
        Objects.requireNonNull(status, "status");
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    public Farmer withStatus(Status newStatus) {
        return new Farmer(idNumber, firstName, lastName, contact, dateOfBirth, gender, postalAddress,
                district, subDistrict, email, farmSize, farmingArea, newStatus);
    }
}
