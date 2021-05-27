package de.uol.vpp.masterdata.domain.exceptions;

public class StorageServiceException extends Exception {

    public StorageServiceException(String message) {
        super(message);
    }

    public StorageServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
