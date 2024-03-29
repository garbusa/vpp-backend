package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.production.domain.entities.ProductionProducerEntity}
 */
@Getter
@Setter
public class ProductionProducerTypeVO {
    private String value;

    public ProductionProducerTypeVO(String value) throws ProductionException {
        if (value == null || value.isBlank() || value.isEmpty() || (!value.equals("WIND") && !value.equals("WATER") &&
                !value.equals("SOLAR") &&
                !value.equals("OTHER") &&
                !value.equals("STORAGE") &&
                !value.equals("GRID"))) {
            throw new ProductionException("producerType", "Erzeugungswert");
        }
        this.value = value;
    }
}
