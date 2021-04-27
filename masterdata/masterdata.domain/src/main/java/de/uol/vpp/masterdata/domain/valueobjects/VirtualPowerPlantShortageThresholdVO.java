package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import lombok.Getter;

@Getter
public class VirtualPowerPlantShortageThresholdVO {

    private final Double value;

    public VirtualPowerPlantShortageThresholdVO(Double value) throws VirtualPowerPlantException {
        if (value == null || value < 0. || value > 100.) {
            throw new VirtualPowerPlantException("validation for shortage threshold failed");
        }
        this.value = value;
    }
}

