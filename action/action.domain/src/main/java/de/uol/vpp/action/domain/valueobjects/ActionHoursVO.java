package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionHoursVO {
    private Double value;

    public ActionHoursVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("hours", "Action");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
