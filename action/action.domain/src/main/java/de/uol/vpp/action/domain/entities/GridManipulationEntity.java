package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.GridManipulationRatedPowerVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domänen-Entität für Stromnetzmanipulationen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GridManipulationEntity extends AbstractManipulationEntity {
    /**
     * Leistung in kW, die manipuliert wird
     */
    private GridManipulationRatedPowerVO ratedPower;
}
