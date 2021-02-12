package de.uol.vpp.masterdata.domain.producer;

import de.uol.vpp.masterdata.domain.architecture.DomainEntity;
import lombok.Data;

@DomainEntity
@Data
public class ProducerEntity {
    private ProducerId producerId;
    private ProducerPower producerPower;
    private ProducerDimension producerDimension;
    private ProducerType producerType;
}
