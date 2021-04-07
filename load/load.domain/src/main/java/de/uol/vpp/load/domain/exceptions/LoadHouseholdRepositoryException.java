package de.uol.vpp.load.domain.exceptions;

public class LoadHouseholdRepositoryException extends Exception {

    public LoadHouseholdRepositoryException(String message) {
        super(message);
    }

    public LoadHouseholdRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
