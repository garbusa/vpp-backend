package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class SolarForecastDTO {
    private ZonedDateTime timestamp;
    private Double value;
}
