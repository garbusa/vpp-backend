package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WaterEnergyCapacityVO {
    private Double value;

    public WaterEnergyCapacityVO(Double value) throws ProducerException {
        if (value == null || value < 0. || value > 100.) {
            throw new ProducerException("capacity", "WaterEnergy");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
