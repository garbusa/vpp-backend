package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ProducerManipulationEntity}
 */
@Getter
public class ProducerManipulationCapacityVO {
    private Double value;

    public ProducerManipulationCapacityVO(Double value) throws ManipulationException {
        if (value == null || value < 0.) {
            throw new ManipulationException("capacity", "ProducerManipulation");
        }
        this.value = value;
    }
}
