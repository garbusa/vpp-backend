package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class StoragePowerVO {
    private final Double ratedPower;
}
