package de.uol.vpp.action.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Datentransferobjekt für Stromnetzmanipulationen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GridManipulationDTO extends AbstractManipulationDTO {
    private Double ratedPower;
}
