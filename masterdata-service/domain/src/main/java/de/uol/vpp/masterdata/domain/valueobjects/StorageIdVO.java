package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class StorageIdVO {

    private final String id;

    public StorageIdVO(String id) throws StorageException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new StorageException("validation for storage id failed");
        }
        this.id = id;
    }
}
