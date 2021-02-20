package de.uol.vpp.masterdata.infrastructure;

import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.ProductType;
import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.Producer;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerDimension;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerPower;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.ProducerType;
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

    public VirtualPowerPlant toInfrastructure(VirtualPowerPlantAggregate domainEntity) {
        VirtualPowerPlant jpaEntity = new VirtualPowerPlant();
        jpaEntity.setBusinessKey(domainEntity.getVirtualPowerPlantId().getId());
        if (domainEntity.getDecentralizedPowerPlants() != null && !domainEntity.getDecentralizedPowerPlants().isEmpty()) {
            jpaEntity.setDecentralizedPowerPlants(
                    domainEntity.getDecentralizedPowerPlants().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        jpaEntity.setHouseholds(
                domainEntity.getHouseholds().stream().map(this::toInfrastructure)
                        .collect(Collectors.toList())
        );
        jpaEntity.setConfigured(false);
        return jpaEntity;
    }

    public DecentralizedPowerPlant toInfrastructure(DecentralizedPowerPlantAggregate domainEntity) {
        DecentralizedPowerPlant jpaEntity = new DecentralizedPowerPlant();
        jpaEntity.setBusinessKey(domainEntity.getDecentralizedPowerPlantId().getId());
        return jpaEntity;
    }

    public Household toInfrastructure(HouseholdAggregate jpaEntity) {
        Household domainEntity = new Household();
        domainEntity.setBusinessKey(jpaEntity.getHouseholdId().getId());
//        domainEntity.setHouseholds(null);
//        domainEntity.setDecentralizedPowerPlants(null);
//        domainEntity.setConfigured(false);
        return domainEntity;
    }

    public ProducerEntity toDomain(Producer jpaEntity) {
        ProducerEntity domainEntity = new ProducerEntity();
        domainEntity.setProducerId(
                new ProducerIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setProducerDimension(new ProducerDimensionVO(
                jpaEntity.getDimension().getHeight(),
                jpaEntity.getDimension().getWidth(),
                jpaEntity.getDimension().getLength(),
                jpaEntity.getDimension().getWeight()
        ));
        domainEntity.setProducerPower(
                new ProducerPowerVO(jpaEntity.getPower().getRatedPower())
        );
        domainEntity.setProducerType(
                new ProducerTypeVO(
                        ProductType.valueOf(jpaEntity.getType().getProductType()),
                        EnergyType.valueOf(jpaEntity.getType().getEnergyType())
                )
        );
        return domainEntity;
    }

    public Producer toInfrastructure(ProducerEntity domainEntity) {
        Producer jpaEntity = new Producer();
        jpaEntity.setBusinessKey(domainEntity.getProducerId().getId());
        ProducerDimension producerDimension = new ProducerDimension();
        producerDimension.setHeight(domainEntity.getProducerDimension().getHeight());
        producerDimension.setLength(domainEntity.getProducerDimension().getLength());
        producerDimension.setWidth(domainEntity.getProducerDimension().getWidth());
        producerDimension.setWeight(domainEntity.getProducerDimension().getWeight());
        jpaEntity.setDimension(
                producerDimension
        );
        ProducerPower producerPower = new ProducerPower();
        producerPower.setRatedPower(domainEntity.getProducerPower().getRatedPower());
        jpaEntity.setPower(
                producerPower
        );
        ProducerType producerType = new ProducerType();
        producerType.setProductType(domainEntity.getProducerType().getProductType().toString());
        producerType.setEnergyType(domainEntity.getProducerType().getEnergyType().toString());
        jpaEntity.setType(producerType);
        return jpaEntity;
    }

}
