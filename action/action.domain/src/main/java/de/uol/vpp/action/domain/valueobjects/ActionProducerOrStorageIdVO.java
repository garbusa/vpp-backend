package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionEntity}
 */
@Getter
public class ActionProducerOrStorageIdVO {
    private String value;

    public ActionProducerOrStorageIdVO(String value) throws ActionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ActionException("producerOrStorage", "Action");
        }
        this.value = value;
    }
}
