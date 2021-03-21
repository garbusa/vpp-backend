package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class StorageTypeVO {

    private final EnergyType energyType;

    public StorageTypeVO(String energyType) throws StorageException {
        if (energyType == null || !EnergyType.isValid(energyType)) {
            throw new StorageException("validation for storage type failed");
        }
        this.energyType = EnergyType.valueOf(energyType);
    }
}

