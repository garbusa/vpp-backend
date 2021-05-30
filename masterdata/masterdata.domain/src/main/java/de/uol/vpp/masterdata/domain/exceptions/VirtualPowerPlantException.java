package de.uol.vpp.masterdata.domain.exceptions;

public class VirtualPowerPlantException extends Exception {

    public VirtualPowerPlantException(String attribute) {
        super("Validierung des " + attribute + "-Attributs der Entität VK ist fehlgeschlagen.");
    }

    public VirtualPowerPlantException(String attribute, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität VK ist fehlgeschlagen.", cause);
    }
}
