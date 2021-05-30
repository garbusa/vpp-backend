package de.uol.vpp.load.domain.exceptions;

public class LoadException extends Exception {

    public LoadException(String attribute, String entity) {
        super("Validierung des " + attribute + "-Attributs der Entität " + entity + " ist fehlgeschlagen.");
    }

    public LoadException(String attribute, String entity, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität " + entity + " ist fehlgeschlagen.", cause);
    }
}
