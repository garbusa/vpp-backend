package de.uol.vpp.action.infrastructure.rest.exceptions;

public class MasterdataRestClientException extends Exception {
    public MasterdataRestClientException(String message) {
        super(message);
    }

    public MasterdataRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
