package de.uol.vpp.masterdata.domain.exceptions;

public class VirtualPowerPlantServiceException extends Exception {

    public VirtualPowerPlantServiceException(String message) {
        super(message);
    }

    public VirtualPowerPlantServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
