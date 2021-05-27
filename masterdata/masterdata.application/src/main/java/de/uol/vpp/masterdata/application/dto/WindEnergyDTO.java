package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

/**
 * Datentransferobjekt zw. Benutzeroberfläche und Planungssystem
 * Siehe {@link de.uol.vpp.masterdata.domain.entities.WindEnergyEntity}
 */
@Data
public class WindEnergyDTO {
    private String windEnergyId;
    private Double longitude;
    private Double latitude;
    private Double efficiency;
    private Double capacity;
    private Double radius;
    private Double height;
}
