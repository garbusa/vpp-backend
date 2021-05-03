package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WindEnergyIdVO {


    private final String value;

    public WindEnergyIdVO(String value) throws ProducerException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ProducerException("windEnergyId", "WindEnergy");
        }
        this.value = value.toUpperCase();
    }

}
