package de.uol.vpp.load.domain.exceptions;

public class LoadServiceException extends Exception {

    public LoadServiceException(String message) {
        super(message);
    }

    public LoadServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
