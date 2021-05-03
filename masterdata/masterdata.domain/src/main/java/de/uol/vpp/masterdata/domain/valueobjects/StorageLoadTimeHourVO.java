package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;

@Getter
public class StorageLoadTimeHourVO {
    private final Double value;

    public StorageLoadTimeHourVO(Double value) throws StorageException {
        if (value < 0) {
            throw new StorageException("loadTimeHour");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
