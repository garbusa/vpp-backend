package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarEnergyIdVO {


    private final String value;

    public SolarEnergyIdVO(String value) throws ProducerException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ProducerException("solarEnergyId", "SolarEnergy");
        }
        this.value = value.toUpperCase();
    }

}
