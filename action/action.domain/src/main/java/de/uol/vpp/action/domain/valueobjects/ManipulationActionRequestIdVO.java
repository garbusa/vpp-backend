package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.AbstractManipulationEntity}
 */
@Data
public class ManipulationActionRequestIdVO {
    private String value;

    public ManipulationActionRequestIdVO(String value) throws ManipulationException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ManipulationException("actionRequestId", "Manipulation");
        }
        this.value = value;
    }
}
