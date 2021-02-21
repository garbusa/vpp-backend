package de.uol.vpp.masterdata.infrastructure;

import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.ProductType;
import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import de.uol.vpp.masterdata.infrastructure.entities.*;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.*;
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

    public ConsumerEntity toDomain(Consumer jpaEntity) {
        ConsumerEntity domainEntity = new ConsumerEntity();
        domainEntity.setConsumerId(
                new ConsumerIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setConsumerPower(
                new ConsumerPowerVO(jpaEntity.getConsumerPower().getConsumingPower())
        );
        return domainEntity;
    }

    public Consumer toInfrastructure(ConsumerEntity domainEntity) {
        Consumer jpaEntity = new Consumer();
        jpaEntity.setBusinessKey(domainEntity.getConsumerId().getId());
        ConsumerPower consumerPower = new ConsumerPower();
        consumerPower.setConsumingPower(domainEntity.getConsumerPower().getConsumingPower());
        jpaEntity.setConsumerPower(
                consumerPower
        );
        return jpaEntity;
    }

    public StorageEntity toDomain(Storage jpaEntity) {
        StorageEntity domainEntity = new StorageEntity();
        domainEntity.setStorageId(
                new StorageIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setStoragePower(
                new StoragePowerVO(jpaEntity.getStoragePower().getRatedPower())
        );
        domainEntity.setStorageType(
                new StorageTypeVO(
                        EnergyType.valueOf(jpaEntity.getStorageType().getEnergyType())
                )
        );
        return domainEntity;
    }

    public Storage toInfrastructure(StorageEntity domainEntity) {
        Storage jpaEntity = new Storage();
        jpaEntity.setBusinessKey(domainEntity.getStorageId().getId());
        StoragePower storagePower = new StoragePower();
        storagePower.setRatedPower(domainEntity.getStoragePower().getRatedPower());
        jpaEntity.setStoragePower(storagePower);
        StorageType storageType = new StorageType();
        storageType.setEnergyType(domainEntity.getStorageType().getEnergyType().toString());
        jpaEntity.setStorageType(storageType);
        return jpaEntity;
    }
}