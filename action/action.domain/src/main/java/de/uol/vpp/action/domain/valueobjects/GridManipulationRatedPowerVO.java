package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class GridManipulationRatedPowerVO {
    private Double value;

    public GridManipulationRatedPowerVO(Double value) throws ManipulationException {
        if (value == null || value < 0.) {
            throw new ManipulationException("ratedPower", "GridManipulation");
        }
        this.value = value;
    }
}
