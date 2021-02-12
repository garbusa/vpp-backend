package de.uol.vpp.masterdata.domain.producer;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class ProducerDimension {
    private Double height;
    private Double width;
    private Double length;
    private Double weight;
}
