package de.uol.vpp.masterdata.domain.exceptions;

public class HouseholdServiceException extends Exception {

    public HouseholdServiceException(String message) {
        super(message);
    }

    public HouseholdServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
