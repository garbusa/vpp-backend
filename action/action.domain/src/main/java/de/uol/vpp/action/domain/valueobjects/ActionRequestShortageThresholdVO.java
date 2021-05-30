package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.aggregates.ActionRequestAggregate}
 */
@Getter
public class ActionRequestShortageThresholdVO {
    private Double value;

    public ActionRequestShortageThresholdVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("shortageThreshold", "Maßnahmenabfrage");
        }
        this.value = value;
    }
}
