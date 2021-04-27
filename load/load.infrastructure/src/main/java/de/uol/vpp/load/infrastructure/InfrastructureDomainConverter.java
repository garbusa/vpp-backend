package de.uol.vpp.load.infrastructure;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.valueobjects.*;
import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.entities.ELoadHousehold;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfrastructureDomainConverter {

    public LoadAggregate toDomain(ELoad jpaEntity) throws LoadException {
        LoadAggregate domainEntity = new LoadAggregate();
        domainEntity.setLoadActionRequestId(
                new LoadActionRequestIdVO(jpaEntity.getActionRequestTimestamp().getActionRequestId())
        );
        domainEntity.setLoadVirtualPowerPlantId(new LoadVirtualPowerPlantIdVO(jpaEntity.getVirtualPowerPlantId()));
        domainEntity.setLoadStartTimestamp(new LoadStartTimestampVO(jpaEntity.getActionRequestTimestamp().getTimestamp().toEpochSecond()));
        List<LoadHouseholdEntity> loadHouseholds = new ArrayList<>();
        for (ELoadHousehold eLoadHousehold : jpaEntity.getHouseholds()) {
            loadHouseholds.add(this.toDomain(eLoadHousehold));
        }
        domainEntity.setLoadHouseholdEntities(loadHouseholds);
        return domainEntity;
    }

    public LoadHouseholdEntity toDomain(ELoadHousehold jpaEntity) throws LoadException {
        LoadHouseholdEntity domainEntity = new LoadHouseholdEntity();
        domainEntity.setLoadHouseholdId(new LoadHouseholdIdVO(jpaEntity.getHouseholdId()));
        domainEntity.setLoadHouseholdStartTimestamp(new LoadHouseholdStartTimestampVO(jpaEntity.getTimestamp().toEpochSecond()));
        domainEntity.setLoadHouseholdValueVO(new LoadHouseholdValueVO(jpaEntity.getHouseholdLoad()));
        domainEntity.setLoadHouseholdMemberAmount(new LoadHouseholdMemberAmountVO(jpaEntity.getHouseholdMemberAmount()));
        return domainEntity;
    }

    public ELoad toInfrastructure(LoadAggregate domainEntity) {
        ELoad jpaEntity = new ELoad();
        jpaEntity.setActionRequestTimestamp(new ELoad.ActionRequestTimestamp(
                domainEntity.getLoadActionRequestId().getId(),
                domainEntity.getLoadStartTimestamp().getTimestamp()
        ));
        jpaEntity.setVirtualPowerPlantId(domainEntity.getLoadVirtualPowerPlantId().getId());
        if (domainEntity.getLoadHouseholdEntities() != null && !domainEntity.getLoadHouseholdEntities().isEmpty()) {
            jpaEntity.setHouseholds(
                    domainEntity.getLoadHouseholdEntities().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        } else {
            jpaEntity.setHouseholds(
                    new ArrayList<>()
            );
        }
        return jpaEntity;
    }

    public ELoadHousehold toInfrastructure(LoadHouseholdEntity domainEntity) {
        ELoadHousehold jpaEntity = new ELoadHousehold();
        jpaEntity.setHouseholdId(domainEntity.getLoadHouseholdId().getId());
        jpaEntity.setHouseholdMemberAmount(domainEntity.getLoadHouseholdMemberAmount().getAmount());
        jpaEntity.setHouseholdLoad(domainEntity.getLoadHouseholdValueVO().getValue());
        jpaEntity.setTimestamp(domainEntity.getLoadHouseholdStartTimestamp().getTimestamp());
        return jpaEntity;
    }
}
