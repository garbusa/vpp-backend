package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class StorageManipulationStorageIdVO {
    private String value;

    public StorageManipulationStorageIdVO(String value) throws ManipulationException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ManipulationException("storageId", "StorageManipulation");
        }
        this.value = value.toUpperCase();
    }
}
