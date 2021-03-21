package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.ProductType;
import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class ProducerTypeVO {

    private final ProductType productType;
    private final EnergyType energyType;

    public ProducerTypeVO(String productType, String energyType) throws ProducerException {
        if (productType == null || energyType == null || !ProductType.isValid(productType) ||
                !EnergyType.isValid(energyType)) {
            throw new ProducerException("validation for producer type failed");
        }

        this.productType = ProductType.valueOf(productType);
        this.energyType = EnergyType.valueOf(energyType);
    }
}

