package de.uol.vpp.masterdata.domain.exceptions;

public class VirtualPowerPlantRepositoryException extends Exception {

    public VirtualPowerPlantRepositoryException(String message) {
        super(message);
    }

    public VirtualPowerPlantRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
