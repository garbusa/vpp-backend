package de.uol.vpp.production.infrastructure.rest.exceptions;

public class SolarRestClientException extends Exception {
    public SolarRestClientException(String message) {
        super(message);
    }

    public SolarRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
