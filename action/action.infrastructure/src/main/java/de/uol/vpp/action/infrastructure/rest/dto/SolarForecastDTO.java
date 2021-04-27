package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class SolarForecastDTO {
    private ZonedDateTime timestamp;
    private Double value;
}
