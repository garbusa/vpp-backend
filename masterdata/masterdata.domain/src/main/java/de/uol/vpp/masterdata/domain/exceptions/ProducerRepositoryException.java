package de.uol.vpp.masterdata.domain.exceptions;

public class ProducerRepositoryException extends Exception {

    public ProducerRepositoryException(String message) {
        super(message);
    }

    public ProducerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
