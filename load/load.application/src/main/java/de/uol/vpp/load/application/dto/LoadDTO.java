package de.uol.vpp.load.application.dto;

import lombok.Data;

import java.util.List;

/**
 * Datentransferobjekt für Lastenwerte-Aggregation
 */
@Data
public class LoadDTO {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private Long startTimestamp;
    private List<LoadHouseholdDTO> households;
}
