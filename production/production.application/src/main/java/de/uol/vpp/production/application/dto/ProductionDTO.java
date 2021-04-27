package de.uol.vpp.production.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductionDTO {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private Long startTimestamp;
    private List<ProductionProducerDTO> producers;
}
