package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarEnergyRatedCapacityVO {

    private Double value;

    public SolarEnergyRatedCapacityVO(Double value) throws ProducerException {
        if (value == null || value < 0.) {
            throw new ProducerException("validation for solar ratedCapacity failed");
        }
        this.value = value;
    }
}
