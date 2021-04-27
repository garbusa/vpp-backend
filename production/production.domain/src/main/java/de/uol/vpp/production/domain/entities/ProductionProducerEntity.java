package de.uol.vpp.production.domain.entities;

import de.uol.vpp.production.domain.valueobjects.*;
import lombok.Data;

@Data
public class ProductionProducerEntity {
    private ProductionProducerIdVO producerId;
    private ProductionProducerTypeVO productionType;
    private ProductionProducerStartTimestampVO startTimestamp;
    private ProductionProducerCurrentValueVO currentValue;
    private ProductionProducerPossibleValueVO possibleValue;
}
