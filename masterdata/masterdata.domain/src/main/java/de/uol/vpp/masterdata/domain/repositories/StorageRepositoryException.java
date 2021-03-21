package de.uol.vpp.masterdata.domain.repositories;

public class StorageRepositoryException extends Exception {

    public StorageRepositoryException(String message) {
        super(message);
    }

    public StorageRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
