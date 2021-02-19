package de.uol.vpp.masterdata.application;

import de.uol.vpp.masterdata.application.dto.DecentralizedPowerPlantDTO;
import de.uol.vpp.masterdata.application.dto.VirtualPowerPlantDTO;
import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ApplicationEntityConverter {

    public VirtualPowerPlantDTO toApplication(VirtualPowerPlantAggregate domainEntity) {
        VirtualPowerPlantDTO dto = new VirtualPowerPlantDTO();
        dto.setVirtualPowerPlantId(domainEntity.getVirtualPowerPlantId().getId());
        dto.setDecentralizedPowerPlants(domainEntity.getDecentralizedPowerPlants().stream()
                .map(this::toApplication).collect(Collectors.toList()));
//        dto.setHouseholds();
        return dto;
    }

    public DecentralizedPowerPlantDTO toApplication(DecentralizedPowerPlantAggregate domainEntity) {
        DecentralizedPowerPlantDTO dto = new DecentralizedPowerPlantDTO();
        dto.setDecentralizedPowerPlantId(domainEntity.getDecentralizedPowerPlantId().getId());
//        dto.setDecentralizedPowerPlants();
//        dto.setHouseholds();
        return dto;
    }

    public VirtualPowerPlantAggregate toDomain(VirtualPowerPlantDTO dto) {
        VirtualPowerPlantAggregate domainEntity = new VirtualPowerPlantAggregate();
        domainEntity.setVirtualPowerPlantId(new VirtualPowerPlantIdVO(dto.getVirtualPowerPlantId()));
//        domainEntity.setHouseholds();
//        domainEntity.setDecentralizedPowerPlants();
        return domainEntity;
    }

    public DecentralizedPowerPlantAggregate toDomain(DecentralizedPowerPlantDTO dto) {
        DecentralizedPowerPlantAggregate domainEntity = new DecentralizedPowerPlantAggregate();
        domainEntity.setDecentralizedPowerPlantId(new DecentralizedPowerPlantIdVO(dto.getDecentralizedPowerPlantId()));
//        domainEntity.setHouseholds();
//        domainEntity.setDecentralizedPowerPlants();
        return domainEntity;
    }
}
