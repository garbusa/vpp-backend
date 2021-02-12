package de.uol.vpp.masterdata.domain.dpp;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import lombok.Data;

@ValueObject
@Data
public class DecentralizedPowerPlantId {
    private String id;
}
