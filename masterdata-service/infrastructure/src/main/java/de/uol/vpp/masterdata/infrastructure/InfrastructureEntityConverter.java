package de.uol.vpp.masterdata.infrastructure;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfrastructureEntityConverter {

    public VirtualPowerPlantAggregate toDomain(VirtualPowerPlant jpaEntity) {
        VirtualPowerPlantAggregate domainEntity = new VirtualPowerPlantAggregate();
        domainEntity.setVirtualPowerPlantId(
                new VirtualPowerPlantIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setDecentralizedPowerPlants(
                jpaEntity.getDecentralizedPowerPlants().stream().map(this::toDomain)
                        .collect(Collectors.toList())
        );
        domainEntity.setHouseholds(
                jpaEntity.getHouseholds().stream().map(this::toDomain)
                        .collect(Collectors.toList())
        );
        domainEntity.setConfigured(jpaEntity.isConfigured());
        return domainEntity;
    }

    public DecentralizedPowerPlantAggregate toDomain(DecentralizedPowerPlant jpaEntity) {
        DecentralizedPowerPlantAggregate domainEntity = new DecentralizedPowerPlantAggregate();
        domainEntity.setDecentralizedPowerPlantId(
                new DecentralizedPowerPlantIdVO(jpaEntity.getBusinessKey())
        );
        return domainEntity;
    }

    public HouseholdAggregate toDomain(Household jpaEntity) {
        HouseholdAggregate domainEntity = new HouseholdAggregate();
        domainEntity.setHouseholdId(
                new HouseholdIdVO(jpaEntity.getBusinessKey())
        );
        return domainEntity;
    }

    public VirtualPowerPlant toInfrastructure(VirtualPowerPlantAggregate jpaEntity) {
        VirtualPowerPlant domainEntity = new VirtualPowerPlant();
        domainEntity.setBusinessKey(jpaEntity.getVirtualPowerPlantId().getId());
        if (jpaEntity.getDecentralizedPowerPlants() != null && !jpaEntity.getDecentralizedPowerPlants().isEmpty()) {
            domainEntity.setDecentralizedPowerPlants(
                    jpaEntity.getDecentralizedPowerPlants().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        domainEntity.setHouseholds(
                jpaEntity.getHouseholds().stream().map(this::toInfrastructure)
                        .collect(Collectors.toList())
        );
        domainEntity.setConfigured(false);
        return domainEntity;
    }

    public DecentralizedPowerPlant toInfrastructure(DecentralizedPowerPlantAggregate jpaEntity) {
        DecentralizedPowerPlant domainEntity = new DecentralizedPowerPlant();
        domainEntity.setBusinessKey(jpaEntity.getDecentralizedPowerPlantId().getId());
        return domainEntity;
    }

    public Household toInfrastructure(HouseholdAggregate jpaEntity) {
        Household domainEntity = new Household();
        domainEntity.setBusinessKey(jpaEntity.getHouseholdId().getId());
//        domainEntity.setHouseholds(null);
//        domainEntity.setDecentralizedPowerPlants(null);
//        domainEntity.setConfigured(false);
        return domainEntity;
    }
}
