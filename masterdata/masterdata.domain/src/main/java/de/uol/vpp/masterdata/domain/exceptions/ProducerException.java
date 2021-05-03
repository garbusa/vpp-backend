package de.uol.vpp.masterdata.domain.exceptions;

public class ProducerException extends Exception {
    public ProducerException(String attribute, String entity) {
        super("Validierung des " + attribute + "-Attributs der Entität " + entity + " fehlgeschlagen");
    }

    public ProducerException(String attribute, String entity, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität " + entity + " fehlgeschlagen", cause);
    }
}
