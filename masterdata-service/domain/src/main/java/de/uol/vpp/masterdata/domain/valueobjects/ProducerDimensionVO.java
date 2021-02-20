package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class ProducerDimensionVO {
    private final Double height;
    private final Double width;
    private final Double length;
    private final Double weight;
}
