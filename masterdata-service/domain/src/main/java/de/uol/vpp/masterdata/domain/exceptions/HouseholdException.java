package de.uol.vpp.masterdata.domain.exceptions;

public class HouseholdException extends Exception {

    public HouseholdException(String message) {
        super(message);
    }

    public HouseholdException(String message, Throwable cause) {
        super(message, cause);
    }
}
