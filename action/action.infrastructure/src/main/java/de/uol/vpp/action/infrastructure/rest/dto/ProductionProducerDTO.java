package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

@Data
public class ProductionProducerDTO {
    private String producerId;
    private String producerType;
    private Long startTimestamp;
    private Double currentValue;
    private Double possibleValue;
}
