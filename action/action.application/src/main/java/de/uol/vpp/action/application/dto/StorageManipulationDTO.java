package de.uol.vpp.action.application.dto;

import lombok.Data;

@Data
public class StorageManipulationDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private String storageId;
    private String type;
    private Double hours;
    private Double ratedPower;
}
