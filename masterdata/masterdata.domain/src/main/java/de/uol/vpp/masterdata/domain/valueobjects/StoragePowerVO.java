package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class StoragePowerVO {

    private final Double ratedPower;

    public StoragePowerVO(Double ratedPower) throws StorageException {
        if (ratedPower == null || ratedPower < 0) {
            throw new StorageException("validation for storage rated power failed");
        }
        this.ratedPower = ratedPower;
    }
}
