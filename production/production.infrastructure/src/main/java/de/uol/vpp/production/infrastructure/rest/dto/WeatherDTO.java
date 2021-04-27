package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class WeatherDTO {
    private ZonedDateTime timestamp;
    private Double windSpeed;
    private Double temperatureCelsius;
    private Double airPressure;
    private Double airHumidity;
}
