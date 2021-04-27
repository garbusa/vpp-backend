package de.uol.vpp.production.domain.exceptions;

public class ProductionRepositoryException extends Exception {
    public ProductionRepositoryException(String message) {
        super(message);
    }

    public ProductionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
