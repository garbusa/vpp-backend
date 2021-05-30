package de.uol.vpp.action.domain.exceptions;

public class ActionException extends Exception {

    public ActionException(String attribute, String entity) {
        super("Validierung für das " + attribute + "-Attribut der Entität " + entity + " ist fehlgeschlagen.");
    }

    public ActionException(String attribute, String entity, Throwable cause) {
        super("Validierung für das " + attribute + "-Attribut der Entität " + entity + " ist fehlgeschlagen.", cause);
    }
}
