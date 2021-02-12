package de.uol.vpp.masterdata.domain.producer;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class ProducerPower {
    private Double ratedPower;
}
