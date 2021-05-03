package de.uol.vpp.production.infrastructure.rest.exceptions;

public class WeatherRestClientException extends Exception {
    public WeatherRestClientException(String message) {
        super(message);
    }

    public WeatherRestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
