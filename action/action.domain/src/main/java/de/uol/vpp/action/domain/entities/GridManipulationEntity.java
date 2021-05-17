package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.GridManipulationActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.GridManipulationRatedPowerVO;
import de.uol.vpp.action.domain.valueobjects.GridManipulationStartEndTimestampVO;
import de.uol.vpp.action.domain.valueobjects.GridManipulationTypeVO;
import lombok.Data;

@Data
public class GridManipulationEntity {
    private GridManipulationActionRequestIdVO actionRequestId;
    private GridManipulationStartEndTimestampVO startEndTimestamp;
    private GridManipulationTypeVO type;
    private GridManipulationRatedPowerVO ratedPower;
}
