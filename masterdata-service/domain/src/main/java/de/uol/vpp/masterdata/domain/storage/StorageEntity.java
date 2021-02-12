package de.uol.vpp.masterdata.domain.storage;

import de.uol.vpp.masterdata.domain.architecture.DomainEntity;
import lombok.Data;

@DomainEntity
@Data
public class StorageEntity {
    private StorageId storageId;
    private StoragePower storagePower;
    private StorageType storageType;
}
