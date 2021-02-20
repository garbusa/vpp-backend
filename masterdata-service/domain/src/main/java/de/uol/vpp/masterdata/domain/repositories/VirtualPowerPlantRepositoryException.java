package de.uol.vpp.masterdata.domain.repositories;

public class VirtualPowerPlantRepositoryException extends Exception {

    public VirtualPowerPlantRepositoryException(String message) {
        super(message);
    }

    public VirtualPowerPlantRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
