package de.uol.vpp.load.application;

import de.uol.vpp.load.application.dto.LoadDTO;
import de.uol.vpp.load.application.dto.LoadHouseholdDTO;
import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Konvertierungsklasse für die Konvertierung zwischen Objekten der Domänen- und Applikationsschicht.
 */
@Component
public class ApplicationDomainConverter {

    public LoadDTO toApplication(LoadAggregate domainEntity) {
        LoadDTO dto = new LoadDTO();
        dto.setActionRequestId(domainEntity.getLoadActionRequestId().getId());
        dto.setVirtualPowerPlantId(domainEntity.getLoadVirtualPowerPlantId().getId());
        dto.setStartTimestamp(domainEntity.getLoadStartTimestamp().getTimestamp().toEpochSecond());
        dto.setHouseholds(domainEntity.getLoadHouseholdEntities().stream().map(this::toApplication).collect(Collectors.toList()));
        return dto;
    }

    public LoadHouseholdDTO toApplication(LoadHouseholdEntity domainEntity) {
        LoadHouseholdDTO dto = new LoadHouseholdDTO();
        dto.setHouseholdId(domainEntity.getLoadHouseholdId().getId());
        dto.setStartTimestamp(domainEntity.getLoadHouseholdStartTimestamp().getTimestamp().toEpochSecond());
        dto.setHouseholdMemberAmount(domainEntity.getLoadHouseholdMemberAmount().getAmount());
        dto.setLoadValue(domainEntity.getLoadHouseholdValueVO().getValue());
        return dto;
    }

}
