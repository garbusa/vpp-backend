package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

/**
 * Datentransferobjekt zw. Benutzeroberfläche und Planungssystem
 * Siehe {@link de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity}
 */
@Data
public class WaterEnergyDTO {
    private String waterEnergyId;
    private Double efficiency;
    private Double capacity;
    private Double density;
    private Double gravity;
    private Double height;
    private Double volumeFlow;
}
