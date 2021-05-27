package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

/**
 * Siehe {@link de.uol.vpp.production.domain.aggregates.ProductionAggregate}
 */
@Getter
@Setter
public class ProductionVirtualPowerPlantIdVO {
    private String value;

    public ProductionVirtualPowerPlantIdVO(String value) throws ProductionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ProductionException("virtualPowerPlantId", "Production");
        }
        this.value = value;
    }
}
