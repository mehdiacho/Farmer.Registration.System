package bw.farmers.model;

/** A login account. The password hash never leaves the data layer as part of this record. */
public record User(long id, String email, String displayName, boolean admin) {
}
