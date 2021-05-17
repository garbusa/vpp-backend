package de.uol.vpp.action.domain.aggregates;

import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.entities.GridManipulationEntity;
import de.uol.vpp.action.domain.entities.ProducerManipulationEntity;
import de.uol.vpp.action.domain.entities.StorageManipulationEntity;
import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

import java.util.List;

@Data
public class ActionRequestAggregate {
    private ActionRequestIdVO actionRequestId;
    private ActionRequestVirtualPowerPlantIdVO virtualPowerPlantId;
    private ActionRequestTimestampVO timestamp;
    private ActionRequestStatusVO status;
    private ActionRequestShortageThresholdVO shortageThreshold;
    private ActionRequestOverflowThresholdVO overflowThreshold;
    private List<ActionCatalogEntity> catalogs;

    private List<ProducerManipulationEntity> producerManipulations;
    private List<StorageManipulationEntity> storageManipulations;
    private List<GridManipulationEntity> gridManipulations;
}