package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.ManipulationTypeEnum;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.AbstractManipulationEntity}
 */
@Data
public class ManipulationTypeVO {
    private ManipulationTypeEnum value;

    public ManipulationTypeVO(ManipulationTypeEnum value) throws ManipulationException {
        if (value == null || !isValidType(value)) {
            throw new ManipulationException("manipulationType", "Manipulation");
        }
        this.value = value;
    }

    private boolean isValidType(ManipulationTypeEnum manipulationType) {
        return manipulationType.equals(ManipulationTypeEnum.PRODUCER_UP) ||
                manipulationType.equals(ManipulationTypeEnum.PRODUCER_DOWN) ||
                manipulationType.equals(ManipulationTypeEnum.STORAGE_LOAD) ||
                manipulationType.equals(ManipulationTypeEnum.STORAGE_UNLOAD) ||
                manipulationType.equals(ManipulationTypeEnum.GRID_LOAD) ||
                manipulationType.equals(ManipulationTypeEnum.GRID_UNLOAD);

    }
}
