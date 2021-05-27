package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

/**
 * Kopie des DTO aus dem Daten-Service
 */
@Data
public class SolarEnergyDTO {
    private String solarEnergyId;
    private Double longitude;
    private Double latitude;
    private Double ratedCapacity;
    private Double capacity;
    private Double alignment;
    private Double slope;
}
