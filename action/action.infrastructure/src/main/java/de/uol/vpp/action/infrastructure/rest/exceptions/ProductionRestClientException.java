package de.uol.vpp.action.infrastructure.rest.exceptions;

public class ProductionRestClientException extends Exception {
    public ProductionRestClientException(String message) {
        super(message);
    }

    public ProductionRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
