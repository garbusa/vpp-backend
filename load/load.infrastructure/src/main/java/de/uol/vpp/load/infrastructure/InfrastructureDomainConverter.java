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
        domainEntity.setLoadVirtualPowerPlantId(
                new LoadVirtualPowerPlantIdVO(jpaEntity.getVppTimestamp().getVppBusinessKey())
        );
        domainEntity.setLoadStartTimestamp(new LoadStartTimestampVO(jpaEntity.getVppTimestamp().getStartTimestamp().toEpochSecond()));
        domainEntity.setLoadIsForecasted(new LoadIsForecastedVO(jpaEntity.isForecasted()));
        domainEntity.setLoadIsOutdated(new LoadIsOutdatedVO(jpaEntity.isOutdated()));
        List<LoadHouseholdEntity> loadHouseholds = new ArrayList<>();
        for (ELoadHousehold eLoadHousehold : jpaEntity.getHouseholds()) {
            loadHouseholds.add(this.toDomain(eLoadHousehold));
        }
        domainEntity.setLoadHouseholdEntities(loadHouseholds);
        return domainEntity;
    }

    public LoadHouseholdEntity toDomain(ELoadHousehold jpaEntity) throws LoadException {
        LoadHouseholdEntity domainEntity = new LoadHouseholdEntity();
        domainEntity.setLoadHouseholdId(new LoadHouseholdIdVO(jpaEntity.getHouseholdTimestamp().getHouseholdBusinessKey()));
        domainEntity.setLoadHouseholdStartTimestamp(new LoadHouseholdStartTimestampVO(jpaEntity.getHouseholdTimestamp().getStartTimestamp().toEpochSecond()));
        domainEntity.setLoadHouseholdValueVO(new LoadHouseholdValueVO(jpaEntity.getLoadValue()));
        domainEntity.setLoadHouseholdMemberAmount(new LoadHouseholdMemberAmountVO(jpaEntity.getHouseholdMemberAmount()));
        return domainEntity;
    }

    public ELoad toInfrastructure(LoadAggregate domainEntity) {
        ELoad jpaEntity = new ELoad();
        jpaEntity.setVppTimestamp(new ELoad.VppTimestamp(
                domainEntity.getLoadVirtualPowerPlantId().getId(),
                domainEntity.getLoadStartTimestamp().getTimestamp()
        ));
        jpaEntity.setForecasted(domainEntity.getLoadIsForecasted().isForecasted());
        jpaEntity.setOutdated(domainEntity.getLoadIsOutdated().isOutdated());
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
        jpaEntity.setHouseholdTimestamp(new ELoadHousehold.HouseholdTimestamp(
                domainEntity.getLoadHouseholdId().getId(),
                domainEntity.getLoadHouseholdStartTimestamp().getTimestamp()
        ));
        jpaEntity.setHouseholdMemberAmount(domainEntity.getLoadHouseholdMemberAmount().getAmount());
        jpaEntity.setLoadValue(domainEntity.getLoadHouseholdValueVO().getValue());
        return jpaEntity;
    }
}
