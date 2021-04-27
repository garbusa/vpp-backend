package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

import java.util.List;

@Data
public class ActionCatalogEntity {
    private ActionRequestIdVO actionRequestIdVO;
    private ActionCatalogStartTimestampVO startTimestamp;
    private ActionCatalogEndTimestampVO endTimestamp;
    private ActionCatalogProblemTypeVO problemType;
    private ActionCatalogCumulativeGapVO cumulativeGap;
    private List<ActionEntity> actions;

}
