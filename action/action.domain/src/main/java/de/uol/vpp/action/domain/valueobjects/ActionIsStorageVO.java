package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionEntity}
 */
@Getter
public class ActionIsStorageVO {
    private Boolean value;

    public ActionIsStorageVO(Boolean value) throws ActionException {
        if (value == null) {
            throw new ActionException("isStorage", "Handlungsempfehlung");
        }
        this.value = value;
    }
}
