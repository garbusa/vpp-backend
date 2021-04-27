package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;

@Getter
public class StorageIdVO {

    private final String value;

    public StorageIdVO(String value) throws StorageException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new StorageException("validation for storage householdLoad failed");
        }
        this.value = value.toUpperCase();
    }
}
