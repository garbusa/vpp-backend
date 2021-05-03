package de.uol.vpp.action.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActionRequestDTO {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private Long timestamp;
    private List<ActionCatalogDTO> catalogs;
    private String status;
}
