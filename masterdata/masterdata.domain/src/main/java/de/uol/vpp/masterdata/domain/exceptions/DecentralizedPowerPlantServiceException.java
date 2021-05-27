package de.uol.vpp.masterdata.domain.exceptions;

public class DecentralizedPowerPlantServiceException extends Exception {

    public DecentralizedPowerPlantServiceException(String message) {
        super(message);
    }

    public DecentralizedPowerPlantServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
