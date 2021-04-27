package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionIsStorageVO {
    private Boolean value;

    public ActionIsStorageVO(Boolean value) throws ActionException {
        if (value == null) {
            throw new ActionException("validation for action currentValue failed");
        }
        this.value = value;
    }
}
