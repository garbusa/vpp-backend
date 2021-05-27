package de.uol.vpp.action.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Datentransferobjekt für Erzeugungsmanipulationen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProducerManipulationDTO extends AbstractManipulationDTO {
    private String producerId;
    private Double capacity;
}
