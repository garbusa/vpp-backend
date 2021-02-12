package de.uol.vpp.masterdata.domain.storage;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class StoragePower {
    private Double ratedPower;
    private Double efficiency;
}
