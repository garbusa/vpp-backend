package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.ProductType;
import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class ProducerTypeVO {
    private final ProductType productType;
    private final EnergyType energyType;
}

