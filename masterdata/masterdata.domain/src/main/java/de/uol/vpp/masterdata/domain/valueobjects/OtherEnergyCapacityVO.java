package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;

@Getter
public class OtherEnergyCapacityVO {
    private Double value;

    public OtherEnergyCapacityVO(Double value) throws ProducerException {
        if (value != null && value < 0. && value > 100.) {
            throw new ProducerException("validation for otherEnergy capacity failed");
        }
        this.value = value;
    }
}
