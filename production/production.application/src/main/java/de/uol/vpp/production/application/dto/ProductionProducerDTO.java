package de.uol.vpp.production.application.dto;

import lombok.Data;

@Data
public class ProductionProducerDTO {
    private String producerId;
    private String producerType;
    private Long startTimestamp;
    private Double currentValue;
    private Double possibleValue;
}
