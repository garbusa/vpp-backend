package de.uol.vpp.action.application.dto;

import lombok.Data;

@Data
public class GridManipulationDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private String type;
    private Double ratedPower;
}
