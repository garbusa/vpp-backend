package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.architecture.DomainEntity;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StoragePowerVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageTypeVO;
import lombok.Data;

@DomainEntity
@Data
public class StorageEntity {
    private StorageIdVO storageId;
    private StoragePowerVO storagePower;
    private StorageTypeVO storageType;
}
