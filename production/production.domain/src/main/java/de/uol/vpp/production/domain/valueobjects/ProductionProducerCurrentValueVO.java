package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.production.domain.entities.ProductionProducerEntity}
 */
@Getter
@Setter
public class ProductionProducerCurrentValueVO {
    private Double value;

    public ProductionProducerCurrentValueVO(Double value) throws ProductionException {
        if (value == null) {
            throw new ProductionException("currentValue", "Erzeugungswert");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
