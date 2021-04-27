package de.uol.vpp.production.domain.exceptions;

public class ProductionServiceException extends Exception {
    public ProductionServiceException(String message) {
        super(message);
    }

    public ProductionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
