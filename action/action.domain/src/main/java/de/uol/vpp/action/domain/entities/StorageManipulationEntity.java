package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

@Data
public class StorageManipulationEntity {
    private StorageManipulationActionRequestIdVO actionRequestId;
    private StorageManipulationStartEndTimestampVO startEndTimestamp;
    private StorageManipulationStorageIdVO storageId;
    private StorageManipulationTypeVO type;
    private StorageManipulationHoursVO hours;
    private StorageManipulationRatedPowerVO ratedPower;

}
