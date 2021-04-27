package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterEnergyGravityVO {
    private Double value;

    public WaterEnergyGravityVO(Double value) throws ProducerException {
        if (value == null || value < 0) {
            throw new ProducerException("validation for water gravity speed failed");
        }
        this.value = value;
    }
}
