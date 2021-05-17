package de.uol.vpp.action.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActionRequestDTO {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private Long timestamp;
    private String status;
    private Double shortageThreshold;
    private Double overflowThreshold;
    private List<ActionCatalogDTO> catalogs;
    private List<ProducerManipulationDTO> producerManipulations;
    private List<StorageManipulationDTO> storageManipulations;
    private List<GridManipulationDTO> gridManipulations;
}
