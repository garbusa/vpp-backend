package de.uol.vpp.action.infrastructure.rest.dto;

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
