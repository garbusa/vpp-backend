package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Kopie des DTO aus dem Daten-Service
 */
@Data
public class SolarForecastDTO {
    private ZonedDateTime timestamp;
    private Double value;
}
