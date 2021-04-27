package de.uol.vpp.production.domain.aggregates;

import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.valueobjects.ProductionActionRequestIdVO;
import de.uol.vpp.production.domain.valueobjects.ProductionStartTimestampVO;
import de.uol.vpp.production.domain.valueobjects.ProductionVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Data
public class ProductionAggregate {
    private ProductionActionRequestIdVO productionActionRequestId;
    private ProductionVirtualPowerPlantIdVO productionVirtualPowerPlantId;
    private ProductionStartTimestampVO productionStartTimestamp;
    private List<ProductionProducerEntity> productionProducers;
}
