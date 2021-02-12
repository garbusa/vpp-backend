package de.uol.vpp.masterdata.domain.vpp;

import de.uol.vpp.masterdata.domain.architecture.Aggregate;
import de.uol.vpp.masterdata.domain.dpp.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.household.HouseholdAggregate;
import lombok.Data;

import java.util.List;

@Aggregate
@Data
public class VirtualPowerPlantAggregate {
    private VirtualPowerPlantId virtualPowerPlantId;
    private List<HouseholdAggregate> households;
    private List<DecentralizedPowerPlantAggregate> decentralizedPowerPlants;
}
