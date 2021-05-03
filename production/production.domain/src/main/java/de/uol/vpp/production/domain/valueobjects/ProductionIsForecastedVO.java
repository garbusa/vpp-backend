package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionIsForecastedVO {
    private Boolean value;

    public ProductionIsForecastedVO(Boolean value) throws ProductionException {
        if (value == null) {
            throw new ProductionException("isForecasted", "Production");
        }
        this.value = value;
    }
}
