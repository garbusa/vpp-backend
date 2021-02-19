package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class ProducerDimensionVO {
    private Double height;
    private Double width;
    private Double length;
    private Double weight;
}
