package de.uol.vpp.production.infrastructure.rest.dto;

import lombok.Data;

@Data
public class WaterEnergyDTO {
    private String waterEnergyId;
    private Double efficiency;
    private Double capacity;
    private Double density;
    private Double gravity;
    private Double height;
    private Double volumeFlow;
}
