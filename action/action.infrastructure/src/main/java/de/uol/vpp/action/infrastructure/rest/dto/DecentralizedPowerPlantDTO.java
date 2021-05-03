package de.uol.vpp.action.infrastructure.rest.dto;

import de.uol.vpp.action.infrastructure.rest.dto.abstracts.DtoHasProducersAndStorages;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecentralizedPowerPlantDTO extends DtoHasProducersAndStorages {
    private String decentralizedPowerPlantId;
}

