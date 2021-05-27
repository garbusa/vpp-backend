package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.StorageManipulationEntity}
 */
@Getter
public class StorageManipulationHoursVO {
    private Double value;

    public StorageManipulationHoursVO(Double value) throws ManipulationException {
        if (value == null || value < 0.) {
            throw new ManipulationException("hours", "StorageManipulation");
        }
        this.value = value;
    }
}
