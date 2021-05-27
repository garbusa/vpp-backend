package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionCatalogEntity}
 */
@Getter
public class ActionCatalogAverageGapVO {
    private Double value;

    public ActionCatalogAverageGapVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("averageGap", "Action");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
