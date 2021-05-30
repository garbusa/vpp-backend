package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.production.domain.entities.ProductionProducerEntity}
 */
@Getter
@Setter
public class ProductionProducerPossibleValueVO {
    private Double value;

    public ProductionProducerPossibleValueVO(Double value) throws ProductionException {
        if (value == null) {
            throw new ProductionException("possibleValue", "Erzeugungswert");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
