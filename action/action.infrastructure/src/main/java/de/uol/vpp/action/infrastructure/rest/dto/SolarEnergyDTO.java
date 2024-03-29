package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

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
