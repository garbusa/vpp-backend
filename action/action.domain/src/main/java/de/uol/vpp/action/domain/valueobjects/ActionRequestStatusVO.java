package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionRequestStatusVO {
    private StatusEnum value;

    public ActionRequestStatusVO(StatusEnum value) throws ActionException {
        if (value == null) {
            throw new ActionException("status", "Action");
        }
        this.value = value;
    }
}
