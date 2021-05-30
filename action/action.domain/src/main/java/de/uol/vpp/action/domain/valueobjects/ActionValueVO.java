package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionEntity}
 */
@Getter
public class ActionValueVO {
    private Double value;

    public ActionValueVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("value", "Handlungsempfehlung");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
