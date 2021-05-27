package de.uol.vpp.masterdata.domain.exceptions;

public class ProducerServiceException extends Exception {

    public ProducerServiceException(String message) {
        super(message);
    }

    public ProducerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
