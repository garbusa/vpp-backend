package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

/**
 * Kopie des DTO aus dem Daten-Service
 */
@Data
public class OtherEnergyDTO {
    private String otherEnergyId;
    private Double ratedCapacity;
    private Double capacity;
}
