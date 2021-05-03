package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionProducerCurrentValueVO {
    private Double value;

    public ProductionProducerCurrentValueVO(Double value) throws ProductionException {
        if (value == null || value < 0) {
            throw new ProductionException("currentValue", "ProductionProducer");
        }
        this.value = Math.round(1000.0 * value) / 1000.0;
    }
}
