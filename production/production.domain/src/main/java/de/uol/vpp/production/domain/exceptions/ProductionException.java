package de.uol.vpp.production.domain.exceptions;

public class ProductionException extends Exception {
    public ProductionException(String message) {
        super(message);
    }

    public ProductionException(String message, Throwable cause) {
        super(message, cause);
    }
}
