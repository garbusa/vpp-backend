package de.uol.vpp.action.domain.exceptions;

public class ActionServiceException extends Exception {

    public ActionServiceException(String message) {
        super(message);
    }

    public ActionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
