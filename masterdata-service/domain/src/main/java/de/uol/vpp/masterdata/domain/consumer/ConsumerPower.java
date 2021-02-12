package de.uol.vpp.masterdata.domain.consumer;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class ConsumerPower {
    private Double consumingPower;
}
