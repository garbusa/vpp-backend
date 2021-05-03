package de.uol.vpp.masterdata.domain.exceptions;

public class ConsumerException extends Exception {

    public ConsumerException(String attribute) {
        super("Validierung des " + attribute + "-Attributs der Entität Consumer fehlgeschlagen");
    }

    public ConsumerException(String attribute, Throwable cause) {
        super("Validierung des " + attribute + "-Attributs der Entität Consumer fehlgeschlagen", cause);
    }
}
