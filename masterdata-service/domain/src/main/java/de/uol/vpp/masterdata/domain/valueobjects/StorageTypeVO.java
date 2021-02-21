package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class StorageTypeVO {
    private final EnergyType energyType;
}

