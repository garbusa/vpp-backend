package de.uol.vpp.masterdata.domain.exceptions;

public class VirtualPowerPlantException extends Exception {

    public VirtualPowerPlantException(String message) {
        super(message);
    }

    public VirtualPowerPlantException(String message, Throwable cause) {
        super(message, cause);
    }
}
