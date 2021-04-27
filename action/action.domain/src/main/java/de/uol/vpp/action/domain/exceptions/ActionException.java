package de.uol.vpp.action.domain.exceptions;

public class ActionException extends Exception {

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
