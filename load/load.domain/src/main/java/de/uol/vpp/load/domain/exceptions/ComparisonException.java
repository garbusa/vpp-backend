package de.uol.vpp.load.domain.exceptions;

public class ComparisonException extends Exception {

    public ComparisonException(String message) {
        super(message);
    }

    public ComparisonException(String message, Throwable cause) {
        super(message, cause);
    }
}
