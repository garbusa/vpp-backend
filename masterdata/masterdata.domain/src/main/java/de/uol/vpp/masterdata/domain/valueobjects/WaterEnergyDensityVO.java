package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterEnergyDensityVO {
    private Double value;

    public WaterEnergyDensityVO(Double value) throws ProducerException {
        if (value == null || value < 0) {
            throw new ProducerException("validation for water density failed");
        }
        this.value = value;
    }
}
