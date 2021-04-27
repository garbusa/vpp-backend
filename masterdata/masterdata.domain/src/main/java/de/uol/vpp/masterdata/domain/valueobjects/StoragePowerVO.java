package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoragePowerVO {

    private final Double value;

    public StoragePowerVO(Double value) throws StorageException {
        if (value == null || value < 0) {
            throw new StorageException("validation for storage rated power failed");
        }
        this.value = value;
    }
}
