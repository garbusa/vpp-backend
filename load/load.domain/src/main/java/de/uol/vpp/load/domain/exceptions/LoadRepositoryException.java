package de.uol.vpp.load.domain.exceptions;

public class LoadRepositoryException extends Exception {

    public LoadRepositoryException(String message) {
        super(message);
    }

    public LoadRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
