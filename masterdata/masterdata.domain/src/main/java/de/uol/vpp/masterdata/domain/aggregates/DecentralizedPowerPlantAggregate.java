package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.aggregates.abstracts.DomainHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecentralizedPowerPlantAggregate extends DomainHasProducersAndStorages {
    private DecentralizedPowerPlantIdVO decentralizedPowerPlantId;
}
