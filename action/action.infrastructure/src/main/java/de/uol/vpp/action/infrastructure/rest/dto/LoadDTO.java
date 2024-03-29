package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoadDTO {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private Long startTimestamp;
    private List<LoadHouseholdDTO> households;
}
