package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Setter
@Getter
public class DecentralizedPowerPlantIdVO {

    private final String id;

    public DecentralizedPowerPlantIdVO(String id) throws DecentralizedPowerPlantException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new DecentralizedPowerPlantException("validation for dpp id failed");
        }
        this.id = id;
    }
}
