package de.uol.vpp.action.domain.exceptions;

public class ManipulationException extends Exception {

    public ManipulationException(String attribute, String entity) {
        super("Validierung für das " + attribute + "-Attribut der Entität " + entity + " fehlgeschlagen");
    }

    public ManipulationException(String attribute, String entity, Throwable cause) {
        super("Validierung für das " + attribute + "-Attribut der Entität " + entity + " fehlgeschlagen", cause);
    }
}
