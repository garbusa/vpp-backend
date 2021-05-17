package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

@Data
public class ProducerManipulationEntity {
    private ProducerManipulationActionRequestIdVO actionRequestId;
    private ProducerManipulationStartEndTimestampVO startEndTimestamp;
    private ProducerManipulationProducerIdVO producerId;
    private ProducerManipulationTypeVO type;
    private ProducerManipulationCapacityVO capacity;
}
