package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionFinishedVO {
    private Boolean value;

    public ActionFinishedVO(Boolean value) throws ActionException {
        if (value == null) {
            throw new ActionException("validation for action finished failed");
        }
        this.value = value;
    }
}
