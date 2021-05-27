package de.uol.vpp.action.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Datentransferobjekt für Speichermanipulationen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StorageManipulationDTO extends AbstractManipulationDTO {
    private String storageId;
    private Double hours;
    private Double ratedPower;
}
