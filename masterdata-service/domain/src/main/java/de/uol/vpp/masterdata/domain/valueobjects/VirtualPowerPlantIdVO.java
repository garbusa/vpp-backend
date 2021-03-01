package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class VirtualPowerPlantIdVO {

    private final String id;

    public VirtualPowerPlantIdVO(String id) throws VirtualPowerPlantException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new VirtualPowerPlantException("validation for vpp id failed");
        }
        this.id = id;
    }
}
