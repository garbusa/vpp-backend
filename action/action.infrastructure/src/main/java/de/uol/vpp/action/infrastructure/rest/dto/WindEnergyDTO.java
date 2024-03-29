package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

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
