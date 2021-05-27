package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

/**
 * Datentransferobjekt zw. Benutzeroberfläche und Planungssystem
 * Siehe {@link de.uol.vpp.masterdata.domain.entities.StorageEntity}
 */
@Data
public class StorageDTO {
    private String storageId;
    private Double ratedPower;
    private Double capacity;
    private Double loadTimeHour;
}
