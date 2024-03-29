package de.uol.vpp.masterdata.domain.exceptions;

public class DecentralizedPowerPlantException extends Exception {

    public DecentralizedPowerPlantException(String attribute) {
        super("Validierung des " + attribute + "-Attributs der Entität DK ist fehlgeschlagen.");
    }

    public DecentralizedPowerPlantException(String attribute, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität DK ist fehlgeschlagen.", cause);
    }
}
