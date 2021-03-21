package de.uol.vpp.load.domain.exceptions;

public class HouseholdLoadException extends Exception {


    public HouseholdLoadException(String message) {
        super(message);
    }

    public HouseholdLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
