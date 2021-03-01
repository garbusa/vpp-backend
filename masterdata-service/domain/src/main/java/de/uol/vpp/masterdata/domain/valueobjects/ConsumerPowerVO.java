package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.ConsumerException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Setter
@Getter
public class ConsumerPowerVO {

    private final Double consumingPower;

    public ConsumerPowerVO(Double consumingPower) throws ConsumerException {
        if (consumingPower == null || consumingPower < 0) {
            throw new ConsumerException("validation for consumer power failed");
        }
        this.consumingPower = consumingPower;
    }
}
