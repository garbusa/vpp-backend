package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

/**
 * Datentransferobjekt zw. Benutzeroberfläche und Planungssystem
 * Siehe {@link de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity}
 */
@Data
public class OtherEnergyDTO {
    private String otherEnergyId;
    private Double ratedCapacity;
    private Double capacity;
}
