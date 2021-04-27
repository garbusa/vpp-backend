package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.valueobjects.StorageCapacityVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageLoadTimeHourVO;
import de.uol.vpp.masterdata.domain.valueobjects.StoragePowerVO;
import lombok.Data;

@Data
public class StorageEntity {
    private StorageIdVO storageId;
    private StoragePowerVO storagePower;
    private StorageCapacityVO storageCapacity;
    private StorageLoadTimeHourVO loadTimeHour;
}
