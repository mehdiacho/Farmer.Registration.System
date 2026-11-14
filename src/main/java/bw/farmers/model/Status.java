package bw.farmers.model;

/** Where a farmer's registration stands. New sign-ups start as {@link #PENDING}. */
public enum Status {
    PENDING("Pending"),
    APPROVED("Approved"),
    DECLINED("Declined");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
