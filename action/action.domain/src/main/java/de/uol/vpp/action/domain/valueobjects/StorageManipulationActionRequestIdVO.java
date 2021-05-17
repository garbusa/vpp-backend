package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class StorageManipulationActionRequestIdVO {
    private String value;

    public StorageManipulationActionRequestIdVO(String value) throws ManipulationException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ManipulationException("actionRequestId", "StorageManipulation");
        }
        this.value = value;
    }
}
