package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.production.domain.entities.ProductionProducerEntity}
 */
@Getter
@Setter
public class ProductionProducerIdVO {
    private String value;

    public ProductionProducerIdVO(String value) throws ProductionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ProductionException("producerId", "Erzeugungswert");
        }
        this.value = value;
    }
}
