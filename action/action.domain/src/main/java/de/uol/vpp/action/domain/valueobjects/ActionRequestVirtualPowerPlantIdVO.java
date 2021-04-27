package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionRequestVirtualPowerPlantIdVO {
    private String value;

    public ActionRequestVirtualPowerPlantIdVO(String value) throws ActionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ActionException("validation for vpp Id failed");
        }
        this.value = value;
    }
}
