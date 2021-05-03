package de.uol.vpp.masterdata.domain.exceptions;

public class HouseholdException extends Exception {

    public HouseholdException(String attribute) {
        super("Validierung des " + attribute + "-Attributs der Entität Household fehlgeschlagen");
    }

    public HouseholdException(String attribute, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität Household fehlgeschlagen", cause);
    }
}
