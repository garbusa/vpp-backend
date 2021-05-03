package de.uol.vpp.production.domain.exceptions;

public class ProductionException extends Exception {
    public ProductionException(String attribute, String entity) {
        super("Validierung des " + attribute + "-Attributs der Entität " + entity + " fehlgeschlagen");
    }

    public ProductionException(String attribute, String entity, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität " + entity + " fehlgeschlagen", cause);
    }
}
