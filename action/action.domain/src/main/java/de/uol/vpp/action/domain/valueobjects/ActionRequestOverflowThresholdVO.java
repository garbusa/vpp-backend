package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionRequestOverflowThresholdVO {
    private Double value;

    public ActionRequestOverflowThresholdVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("overflowThreshold", "ActionRequest");
        }
        this.value = value;
    }
}