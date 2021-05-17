package de.uol.vpp.action.application.dto;

import lombok.Data;

@Data
public class ProducerManipulationDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private String producerId;
    private String type;
    private Double capacity;
}
