package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DecentralizedPowerPlantIdVO {

    private final String value;

    public DecentralizedPowerPlantIdVO(String value) throws DecentralizedPowerPlantException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new DecentralizedPowerPlantException("decentralizedPowerPlantId");
        }
        this.value = value.toUpperCase();
    }
}
