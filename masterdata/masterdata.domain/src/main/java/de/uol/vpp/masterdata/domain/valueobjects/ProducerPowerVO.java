package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Setter
@Getter
public class ProducerPowerVO {

    private final Double ratedPower;

    public ProducerPowerVO(Double ratedPower) throws ProducerException {
        if (ratedPower == null || ratedPower < 0) {
            throw new ProducerException("validation for producer rated power failed");
        }
        this.ratedPower = ratedPower;
    }
}
