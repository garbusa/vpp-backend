package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

/**
 * Datentransferobjekt zw. Benutzeroberfläche und Planungssystem
 * Siehe {@link de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity}
 */
@Data
public class SolarEnergyDTO {
    private String solarEnergyId;
    private Double longitude;
    private Double latitude;
    private Double ratedCapacity;
    private Double capacity;
    private Double alignment;
    private Double slope;
}
