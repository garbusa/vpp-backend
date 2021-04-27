package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

@Data
public class OtherEnergyDTO {
    private String otherEnergyId;
    private Double ratedCapacity;
    private Double capacity;
}
