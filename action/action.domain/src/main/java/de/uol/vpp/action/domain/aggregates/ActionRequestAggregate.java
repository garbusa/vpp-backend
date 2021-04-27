package de.uol.vpp.action.domain.aggregates;

import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.valueobjects.ActionFinishedVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestTimestampVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Data
public class ActionRequestAggregate {
    private ActionRequestIdVO actionRequestId;
    private ActionRequestVirtualPowerPlantIdVO virtualPowerPlantId;
    private ActionRequestTimestampVO timestamp;
    private List<ActionCatalogEntity> catalogs;
    private ActionFinishedVO finished;
}