package de.uol.vpp.production.application.dto;

import lombok.Data;

/**
 * Datenaustauschobject für Erzeugung einer Erzeugungsanlage innerhalb eines Zeitstempels
 * Für mehr Informationen siehe {@link de.uol.vpp.production.domain.entities.ProductionProducerEntity}
 */
@Data
public class ProductionProducerDTO {
    private String producerId;
    private String producerType;
    private Long startTimestamp;
    private Double currentValue;
    private Double possibleValue;
}
