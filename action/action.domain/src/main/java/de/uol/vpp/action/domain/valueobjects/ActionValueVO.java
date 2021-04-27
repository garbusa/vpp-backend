package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionValueVO {
    private Double value;

    public ActionValueVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("validation for action currentValue failed");
        }
        this.value = value;
    }
}
