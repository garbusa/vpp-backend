package de.uol.vpp.load.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoadDTO {
    private String virtualPowerPlantId;
    private Long startTimestamp;
    private boolean forecasted;
    private boolean outdated;
    private List<LoadHouseholdDTO> households;
}
