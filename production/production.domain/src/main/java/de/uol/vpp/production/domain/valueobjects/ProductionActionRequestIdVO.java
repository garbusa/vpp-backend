package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;

@Getter
public class ProductionActionRequestIdVO {

    private final String value;

    public ProductionActionRequestIdVO(String value) throws ProductionException {
        if (value == null || value.isBlank() || value.isEmpty()) {
            throw new ProductionException("actionRequestId", "Production");
        }
        this.value = value;
    }
}
