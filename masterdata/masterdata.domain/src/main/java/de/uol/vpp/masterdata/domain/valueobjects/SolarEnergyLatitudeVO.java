package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarEnergyLatitudeVO {

    private Double value;

    public SolarEnergyLatitudeVO(Double value) throws ProducerException {
        if (value == null || value < -90. || value > 90.) {
            throw new ProducerException("latitude", "SolarEnergy");
        }
        this.value = value;
    }
}
