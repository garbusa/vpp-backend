package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WindEnergyCapacityVO {
    private Double value;

    public WindEnergyCapacityVO(Double value) throws ProducerException {
        if (value == null || value < 0. || value > 100.) {
            throw new ProducerException("validation for wind capacity failed");
        }
        this.value = value;
    }
}
