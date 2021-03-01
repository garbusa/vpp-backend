package de.uol.vpp.masterdata.domain.repositories;

public class ConsumerRepositoryException extends Exception {

    public ConsumerRepositoryException(String message) {
        super(message);
    }

    public ConsumerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
