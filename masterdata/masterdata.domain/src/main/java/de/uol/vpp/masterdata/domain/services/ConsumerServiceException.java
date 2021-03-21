package de.uol.vpp.masterdata.domain.services;

public class ConsumerServiceException extends Exception {

    public ConsumerServiceException(String message) {
        super(message);
    }

    public ConsumerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
