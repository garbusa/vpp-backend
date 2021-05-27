package de.uol.vpp.action.application.dto;

import lombok.Data;

@Data
abstract class AbstractManipulationDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private String type;
}
