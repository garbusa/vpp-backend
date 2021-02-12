package de.uol.vpp.masterdata.domain.consumer;

import de.uol.vpp.masterdata.domain.architecture.DomainEntity;
import lombok.Data;

@DomainEntity
@Data
public class ConsumerEntity {
    private ConsumerId consumerId;
    private ConsumerPower consumerPower;
}
