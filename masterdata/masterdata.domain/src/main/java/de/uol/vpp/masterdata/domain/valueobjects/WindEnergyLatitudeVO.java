package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WindEnergyLatitudeVO {

    private Double value;

    public WindEnergyLatitudeVO(Double value) throws ProducerException {
        if (value == null || value < -90. || value > 90.) {
            throw new ProducerException("validation for wind latidude failed");
        }
        this.value = value;
    }
}
