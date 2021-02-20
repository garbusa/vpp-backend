package de.uol.vpp.masterdata.domain.repositories;

public class HouseholdRepositoryException extends Exception {

    public HouseholdRepositoryException(String message) {
        super(message);
    }

    public HouseholdRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
