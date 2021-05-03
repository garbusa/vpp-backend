package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarEnergyAreaVO {
    private Double value;

    public SolarEnergyAreaVO(Double value) throws ProducerException {
        if (value == null || value < 0.) {
            throw new ProducerException("area", "SolarEnergy");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
