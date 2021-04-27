package de.uol.vpp.load.domain.aggregates;

import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.valueobjects.LoadActionRequestIdVO;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Data
public class LoadAggregate {
    private LoadActionRequestIdVO loadActionRequestId;
    private LoadVirtualPowerPlantIdVO loadVirtualPowerPlantId;
    private LoadStartTimestampVO loadStartTimestamp;
    private List<LoadHouseholdEntity> loadHouseholdEntities;
}
