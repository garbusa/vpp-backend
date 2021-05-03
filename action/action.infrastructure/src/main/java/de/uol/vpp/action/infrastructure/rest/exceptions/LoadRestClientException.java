package de.uol.vpp.action.infrastructure.rest.exceptions;

public class LoadRestClientException extends Exception {
    public LoadRestClientException(String message) {
        super(message);
    }

    public LoadRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
