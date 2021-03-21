package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class StorageStatusVO {

    private final Double capacity;

    public StorageStatusVO(Double capacity) throws StorageException {
        if (capacity == null || capacity < 0 || capacity > 100) {
            throw new StorageException("validation for storage status failed");
        }
        this.capacity = capacity;
    }
}
