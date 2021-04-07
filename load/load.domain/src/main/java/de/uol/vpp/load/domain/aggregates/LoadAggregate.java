package de.uol.vpp.load.domain.aggregates;

import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.valueobjects.LoadIsForecastedVO;
import de.uol.vpp.load.domain.valueobjects.LoadIsOutdatedVO;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Data
public class LoadAggregate {
    private LoadVirtualPowerPlantIdVO loadVirtualPowerPlantId;
    private LoadStartTimestampVO loadStartTimestamp;

    private LoadIsForecastedVO loadIsForecasted;
    private LoadIsOutdatedVO loadIsOutdated;
    private List<LoadHouseholdEntity> loadHouseholdEntities;
}
