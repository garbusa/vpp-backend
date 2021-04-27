package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionHoursVO {
    private Double value;

    public ActionHoursVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("validation for action hours failed");
        }
        this.value = value;
    }
}
