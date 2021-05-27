package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.aggregates.abstracts.DomainHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Domänen-Aggregat für dezentralte Kraftwerke
 * Erweitert abstrakte Klasse {@link DomainHasProducersAndStorages}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DecentralizedPowerPlantAggregate extends DomainHasProducersAndStorages {
    /**
     * Identifizierung des DK
     */
    private DecentralizedPowerPlantIdVO decentralizedPowerPlantId;
}
