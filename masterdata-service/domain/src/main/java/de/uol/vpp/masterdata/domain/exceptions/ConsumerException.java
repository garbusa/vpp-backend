package de.uol.vpp.masterdata.domain.exceptions;

public class ConsumerException extends Exception {

    public ConsumerException(String message) {
        super(message);
    }

    public ConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
