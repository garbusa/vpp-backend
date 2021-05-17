package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import lombok.Getter;

@Getter
public class VirtualPowerPlantOverflowThresholdVO {

    private final Double value;

    public VirtualPowerPlantOverflowThresholdVO(Double value) throws VirtualPowerPlantException {
        if (value == null || value < 0.) {
            throw new VirtualPowerPlantException("overflowThreshold");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
