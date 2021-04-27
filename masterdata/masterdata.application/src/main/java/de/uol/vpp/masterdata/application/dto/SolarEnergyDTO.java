package de.uol.vpp.masterdata.application.dto;

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
