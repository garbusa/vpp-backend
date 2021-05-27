package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.ActionTypeEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionEntity}
 */
@Getter
public class ActionTypeVO {
    private ActionTypeEnum value;

    public ActionTypeVO(ActionTypeEnum value) throws ActionException {
        if (value == null) {
            throw new ActionException("actionType", "Action");
        }
        this.value = value;
    }
}
