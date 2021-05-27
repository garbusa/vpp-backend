package de.uol.vpp.masterdata.domain.exceptions;

public class DecentralizedPowerPlantRepositoryException extends Exception {

    public DecentralizedPowerPlantRepositoryException(String message) {
        super(message);
    }

    public DecentralizedPowerPlantRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
