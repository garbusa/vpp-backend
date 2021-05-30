package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.aggregates.ActionRequestAggregate}
 */
@Getter
public class ActionRequestStatusVO {
    private StatusEnum value;

    public ActionRequestStatusVO(StatusEnum value) throws ActionException {
        if (value == null) {
            throw new ActionException("status", "Handlungsempfehlung");
        }
        this.value = value;
    }
}
