package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantOverflowThresholdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantPublishedVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantShortageThresholdVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VirtualPowerPlantAggregate {
    private VirtualPowerPlantIdVO virtualPowerPlantId;
    private List<HouseholdAggregate> households = new ArrayList<>();
    private List<DecentralizedPowerPlantAggregate> decentralizedPowerPlants = new ArrayList<>();
    private VirtualPowerPlantPublishedVO published;
    private VirtualPowerPlantShortageThresholdVO shortageThreshold;
    private VirtualPowerPlantOverflowThresholdVO overflowThreshold;
}
