package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterEnergyEfficiencyVO {

    private Double value;

    public WaterEnergyEfficiencyVO(Double value) throws ProducerException {
        if (value == null || value < 0. || value > 100.) {
            throw new ProducerException("efficiency", "WaterEnergy");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
