package de.uol.vpp.production.domain.exceptions;

public class ProductionProducerRepositoryException extends Exception {
    public ProductionProducerRepositoryException(String message) {
        super(message);
    }

    public ProductionProducerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
