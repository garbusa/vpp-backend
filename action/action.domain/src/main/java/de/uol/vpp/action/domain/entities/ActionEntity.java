package de.uol.vpp.action.domain.entities;

import de.uol.vpp.action.domain.valueobjects.*;
import lombok.Data;

@Data
public class ActionEntity {
    private ActionRequestIdVO actionRequestId;
    private ActionCatalogStartTimestampVO startTimestamp;
    private ActionCatalogEndTimestampVO endTimestamp;
    private ActionTypeVO actionType;
    private ActionProducerOrStorageIdVO producerOrStorageId;
    private ActionIsStorageVO isStorage;
    private ActionValueVO actionValue;
    private ActionHoursVO hours;
}
