package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionCatalogCumulativeGapVO {
    private Double value;

    public ActionCatalogCumulativeGapVO(Double value) throws ActionException {
        if (value == null || value < 0.) {
            throw new ActionException("validation for action catalog cumulativeGap failed");
        }
        this.value = value;
    }
}
