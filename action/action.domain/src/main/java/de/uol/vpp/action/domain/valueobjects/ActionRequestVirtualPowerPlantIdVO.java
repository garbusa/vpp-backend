package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.aggregates.ActionRequestAggregate}
 */
@Getter
public class ActionRequestVirtualPowerPlantIdVO {
    private String value;

    public ActionRequestVirtualPowerPlantIdVO(String value) throws ActionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ActionException("virtualPowerPlantId", "ActionRequest");
        }
        this.value = value;
    }
}
