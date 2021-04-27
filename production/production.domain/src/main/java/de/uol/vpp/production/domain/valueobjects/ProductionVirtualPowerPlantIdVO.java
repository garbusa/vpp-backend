package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionVirtualPowerPlantIdVO {
    private String value;

    public ProductionVirtualPowerPlantIdVO(String value) throws ProductionException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ProductionException("validation for actionRequestId failed");
        }
        this.value = value;
    }
}
