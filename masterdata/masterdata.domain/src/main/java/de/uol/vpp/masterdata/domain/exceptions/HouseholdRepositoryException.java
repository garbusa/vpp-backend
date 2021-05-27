package de.uol.vpp.masterdata.domain.exceptions;

public class HouseholdRepositoryException extends Exception {

    public HouseholdRepositoryException(String message) {
        super(message);
    }

    public HouseholdRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
