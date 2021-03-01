package de.uol.vpp.masterdata.domain.utils;

public class PublishException extends Exception {

    public PublishException(String message) {
        super(message);
    }

    public PublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
