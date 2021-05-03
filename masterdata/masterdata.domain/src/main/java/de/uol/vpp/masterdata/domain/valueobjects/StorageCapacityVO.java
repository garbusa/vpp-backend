package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageCapacityVO {

    private final Double value;

    public StorageCapacityVO(Double value) throws StorageException {
        if (value == null || value < 0 || value > 100) {
            throw new StorageException("capacity");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
