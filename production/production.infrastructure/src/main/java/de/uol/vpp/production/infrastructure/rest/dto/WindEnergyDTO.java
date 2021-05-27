package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

/**
 * Kopie des DTO aus dem Daten-Service
 */
@Data
public class WindEnergyDTO {
    private String windEnergyId;
    private Double longitude;
    private Double latitude;
    private Double efficiency;
    private Double capacity;
    private Double radius;
    private Double height;
}
