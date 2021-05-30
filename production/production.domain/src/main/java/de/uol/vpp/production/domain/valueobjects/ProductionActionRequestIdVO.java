package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.production.domain.aggregates.ProductionAggregate}
 */
@Getter
public class ProductionActionRequestIdVO {

    private final String value;

    public ProductionActionRequestIdVO(String value) throws ProductionException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ProductionException("actionRequestId", "Erzeugungsaggregat");
        }
        this.value = value;
    }
}
