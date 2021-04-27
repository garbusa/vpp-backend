package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;

@Getter
public class StorageLoadTimeHourVO {
    private final Double value;

    public StorageLoadTimeHourVO(Double value) throws StorageException {
        if (value < 0) {
            throw new StorageException("validation for storage load time in hours failed");
        }
        this.value = value;
    }
}
