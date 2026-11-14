package bw.farmers.service;

import java.util.List;

/** Thrown when input breaks one or more rules. The messages are written for the person filling the form. */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final List<String> problems;

    public ValidationException(List<String> problems) {
        super(String.join("\n", problems));
        this.problems = List.copyOf(problems);
    }

    public ValidationException(String problem) {
        this(List.of(problem));
    }

    public List<String> problems() {
        return problems;
    }
}
