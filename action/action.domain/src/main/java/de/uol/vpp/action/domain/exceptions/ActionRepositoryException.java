package de.uol.vpp.action.domain.exceptions;

public class ActionRepositoryException extends Exception {

    public ActionRepositoryException(String message) {
        super(message);
    }

    public ActionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
