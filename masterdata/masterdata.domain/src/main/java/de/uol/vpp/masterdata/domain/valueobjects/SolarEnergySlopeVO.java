package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarEnergySlopeVO {
    private Double value;

    public SolarEnergySlopeVO(Double value) throws ProducerException {
        if (value == null || value < 0 || value > 360) {
            throw new ProducerException("slope", "SolarEnergy");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
