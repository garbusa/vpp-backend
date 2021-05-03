package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionRequestIdVO {
    private String value;

    public ActionRequestIdVO(String value) throws ActionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ActionException("actionRequestId", "ActionRequest");
        }
        this.value = value;
    }
}
