package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

@Data
public class StorageDTO {
    private String storageId;
    private Double ratedPower;
    private Double capacity;
    private Double loadTimeHour;
}
