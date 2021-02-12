package de.uol.vpp.masterdata.domain.vpp;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class VirtualPowerPlantId {
    private String id;
}
