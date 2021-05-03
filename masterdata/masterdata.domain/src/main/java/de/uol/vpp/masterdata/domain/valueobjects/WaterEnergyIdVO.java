package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterEnergyIdVO {


    private final String value;

    public WaterEnergyIdVO(String value) throws ProducerException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ProducerException("waterEnergyId", "WaterEnergy");
        }
        this.value = value.toUpperCase();
    }

}
