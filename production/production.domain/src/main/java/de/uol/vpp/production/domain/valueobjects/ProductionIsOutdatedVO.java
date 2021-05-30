package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.production.domain.aggregates.ProductionAggregate}
 */
@Getter
@Setter
public class ProductionIsOutdatedVO {
    private Boolean value;

    public ProductionIsOutdatedVO(Boolean value) throws ProductionException {
        if (value == null) {
            throw new ProductionException("isOutdated", "Erzeugungsaggregat");
        }
        this.value = value;
    }
}
