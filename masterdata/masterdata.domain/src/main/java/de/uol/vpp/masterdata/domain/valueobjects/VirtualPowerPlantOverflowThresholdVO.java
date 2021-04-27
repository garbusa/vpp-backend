package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import lombok.Getter;

@Getter
public class VirtualPowerPlantOverflowThresholdVO {

    private final Double value;

    public VirtualPowerPlantOverflowThresholdVO(Double value) throws VirtualPowerPlantException {
        if (value == null || value < 0. || value > 100.) {
            throw new VirtualPowerPlantException("validation for overflow threshold failed");
        }
        this.value = value;
    }
}
