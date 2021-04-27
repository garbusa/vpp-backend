package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionProducerOrStorageIdVO {
    private String value;

    public ActionProducerOrStorageIdVO(String value) throws ActionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ActionException("validation for action producerOrStorageId failed");
        }
        this.value = value;
    }
}
