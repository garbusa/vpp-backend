package de.uol.vpp.production.application.dto;

import lombok.Data;

import java.util.List;

/**
 * Datenaustauschobject für Erzeugungsaggregation innerhalb eines Zeitstempels
 * Für mehr Informationen siehe {@link de.uol.vpp.production.domain.aggregates.ProductionAggregate}
 */
@Data
public class ProductionDTO {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private Long startTimestamp;
    private List<ProductionProducerDTO> producers;
}
