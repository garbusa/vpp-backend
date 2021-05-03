package de.uol.vpp.production.domain.valueobjects;

import de.uol.vpp.production.domain.exceptions.ProductionException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionIsOutdatedVO {
    private Boolean value;

    public ProductionIsOutdatedVO(Boolean value) throws ProductionException {
        if (value == null) {
            throw new ProductionException("isOutdated", "Production");
        }
        this.value = value;
    }
}
