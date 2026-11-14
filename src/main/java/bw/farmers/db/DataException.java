package bw.farmers.db;

/** An unchecked wrapper for database failures the UI cannot recover from. */
public class DataException extends RuntimeException {
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
