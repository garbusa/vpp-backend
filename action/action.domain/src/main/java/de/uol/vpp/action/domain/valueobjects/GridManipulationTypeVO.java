package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.GridManipulationTypeEnum;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class GridManipulationTypeVO {
    private GridManipulationTypeEnum value;

    public GridManipulationTypeVO(GridManipulationTypeEnum value) throws ManipulationException {
        if (value == null || (!value.equals(GridManipulationTypeEnum.GRID_LOAD) &&
                !value.equals(GridManipulationTypeEnum.GRID_UNLOAD))) {
            throw new ManipulationException("manipulationType", "GridManipulation");
        }
        this.value = value;
    }
}
