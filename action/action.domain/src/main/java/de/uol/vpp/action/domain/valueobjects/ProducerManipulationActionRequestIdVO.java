package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class ProducerManipulationActionRequestIdVO {
    private String value;

    public ProducerManipulationActionRequestIdVO(String value) throws ManipulationException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ManipulationException("actionRequestId", "ProducerManipulation");
        }
        this.value = value;
    }
}
