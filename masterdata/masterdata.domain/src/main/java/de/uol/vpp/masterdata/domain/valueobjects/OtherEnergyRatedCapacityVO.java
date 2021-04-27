package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;

@Getter
public class OtherEnergyRatedCapacityVO {
    private Double value;

    public OtherEnergyRatedCapacityVO(Double value) throws ProducerException {
        if (value != null && value < 0.) {
            throw new ProducerException("validation for otherEnergy rated capacity failed");
        }
        this.value = value;
    }
}
