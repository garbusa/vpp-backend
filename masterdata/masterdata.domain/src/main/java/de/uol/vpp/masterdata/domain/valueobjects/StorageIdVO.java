package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;

/**
 * Ein Value Object ist für die Validierung der Attribute zuständig
 * Für eine Definition des Objektes siehe {@link de.uol.vpp.masterdata.domain.entities.StorageEntity}
 */
@Getter
public class StorageIdVO {

    private final String value;

    public StorageIdVO(String value) throws StorageException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new StorageException("storageId");
        }
        this.value = value.toUpperCase();
    }
}
