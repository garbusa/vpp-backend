package de.uol.vpp.load.domain.aggregates;

import de.uol.vpp.load.domain.entities.HouseholdLoadEntity;
import de.uol.vpp.load.domain.valueobjects.LoadEndTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Data
public class LoadAggregate {
    private LoadVirtualPowerPlantIdVO loadVirtualPowerPlantIdVO;
    private LoadStartTimestampVO loadStartTimestampVO;

    private LoadEndTimestampVO loadEndStartTimestampVO;
    private List<HouseholdLoadEntity> householdLoads;
}
