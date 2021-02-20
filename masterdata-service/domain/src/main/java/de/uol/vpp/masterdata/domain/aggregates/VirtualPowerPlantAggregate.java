package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.architecture.Aggregate;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Aggregate
@Data
public class VirtualPowerPlantAggregate {
    private VirtualPowerPlantIdVO virtualPowerPlantId;
    private List<HouseholdAggregate> households;
    private List<DecentralizedPowerPlantAggregate> decentralizedPowerPlants;
    private boolean configured;
}
