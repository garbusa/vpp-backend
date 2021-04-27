package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WindEnergyEfficiencyVO {

    private Double value;

    public WindEnergyEfficiencyVO(Double value) throws ProducerException {
        if (value == null || value < 0. || value > 100.) {
            throw new ProducerException("validation for wind ratedCapacity failed");
        }
        this.value = value;
    }
}
