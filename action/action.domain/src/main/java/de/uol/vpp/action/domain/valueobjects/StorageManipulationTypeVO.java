package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.StorageManipulationEnum;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class StorageManipulationTypeVO {
    private StorageManipulationEnum value;

    public StorageManipulationTypeVO(StorageManipulationEnum value) throws ManipulationException {
        if (value == null || (!value.equals(StorageManipulationEnum.STORAGE_LOAD) &&
                !value.equals(StorageManipulationEnum.STORAGE_UNLOAD))) {
            throw new ManipulationException("manipulationType", "StorageManipulation");
        }
        this.value = value;
    }
}
