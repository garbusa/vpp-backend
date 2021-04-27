package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionProducerPossibleValueVO {
    private Double value;

    public ProductionProducerPossibleValueVO(Double value) throws ProductionException {
        if (value == null || value < 0) {
            throw new ProductionException("validation for producer householdLoad failed.");
        }
        this.value = value;
    }
}
