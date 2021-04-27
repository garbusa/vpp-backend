package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WindEnergyRadiusVO {
    private Double value;

    public WindEnergyRadiusVO(Double value) throws ProducerException {
        if (value == null || value < 0) {
            throw new ProducerException("validation for wind radius failed");
        }
        this.value = value;
    }
}

