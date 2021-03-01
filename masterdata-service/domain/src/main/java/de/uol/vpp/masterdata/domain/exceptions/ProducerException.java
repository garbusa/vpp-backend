package de.uol.vpp.masterdata.domain.exceptions;

public class ProducerException extends Exception {

    public ProducerException(String message) {
        super(message);
    }

    public ProducerException(String message, Throwable cause) {
        super(message, cause);
    }
}
