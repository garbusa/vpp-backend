package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.StorageManipulationEntity}
 */
@Getter
public class StorageManipulationRatedPowerVO {
    private Double value;

    public StorageManipulationRatedPowerVO(Double value) throws ManipulationException {
        if (value == null || value < 0.) {
            throw new ManipulationException("ratedPower", "StorageManipulation");
        }
        this.value = value;
    }
}
