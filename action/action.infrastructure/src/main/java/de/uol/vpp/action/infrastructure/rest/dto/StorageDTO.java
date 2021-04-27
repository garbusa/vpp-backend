package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

@Data
public class StorageDTO {
    private String storageId;
    private Double ratedPower;
    private Double capacity;
    private Double loadTimeHour;
}
