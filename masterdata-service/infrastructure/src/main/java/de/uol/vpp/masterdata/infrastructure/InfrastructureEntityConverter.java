package de.uol.vpp.masterdata.infrastructure;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import de.uol.vpp.masterdata.infrastructure.entities.*;
import de.uol.vpp.masterdata.infrastructure.entities.embeddables.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfrastructureEntityConverter {

    public VirtualPowerPlantAggregate toDomain(VirtualPowerPlant jpaEntity) throws VirtualPowerPlantException {
        try {
            VirtualPowerPlantAggregate domainEntity = new VirtualPowerPlantAggregate();
            domainEntity.setVirtualPowerPlantId(
                    new VirtualPowerPlantIdVO(jpaEntity.getBusinessKey())
            );
            List<DecentralizedPowerPlantAggregate> list = new ArrayList<>();
            for (DecentralizedPowerPlant decentralizedPowerPlant : jpaEntity.getDecentralizedPowerPlants()) {
                DecentralizedPowerPlantAggregate toDomain = this.toDomain(decentralizedPowerPlant);
                list.add(toDomain);
            }
            domainEntity.setDecentralizedPowerPlants(
                    list
            );
            List<HouseholdAggregate> result = new ArrayList<>();
            for (Household household : jpaEntity.getHouseholds()) {
                HouseholdAggregate toDomain = this.toDomain(household);
                result.add(toDomain);
            }
            domainEntity.setHouseholds(
                    result
            );
            domainEntity.setPublished(new VirtualPowerPlantPublishedVO(jpaEntity.isPublished()));
            return domainEntity;
        } catch (DecentralizedPowerPlantException | HouseholdException e) {
            throw new VirtualPowerPlantException(e.getMessage(), e);
        }
    }

    public DecentralizedPowerPlantAggregate toDomain(DecentralizedPowerPlant jpaEntity) throws DecentralizedPowerPlantException {
        try {
            DecentralizedPowerPlantAggregate domainEntity = new DecentralizedPowerPlantAggregate();
            domainEntity.setDecentralizedPowerPlantId(
                    new DecentralizedPowerPlantIdVO(jpaEntity.getBusinessKey())
            );
            List<ProducerEntity> producers = new ArrayList<>();
            for (Producer producer : jpaEntity.getProducers()) {
                producers.add(this.toDomain(producer));
            }
            domainEntity.setProducers(
                    producers
            );
            List<StorageEntity> storages = new ArrayList<>();
            for (Storage storage : jpaEntity.getStorages()) {
                storages.add(this.toDomain(storage));
            }
            domainEntity.setStorages(
                    storages
            );
            return domainEntity;
        } catch (StorageException | ProducerException e) {
            throw new DecentralizedPowerPlantException(e.getMessage(), e);
        }
    }

    public HouseholdAggregate toDomain(Household jpaEntity) throws HouseholdException {
        try {
            HouseholdAggregate domainEntity = new HouseholdAggregate();
            domainEntity.setHouseholdId(
                    new HouseholdIdVO(jpaEntity.getBusinessKey())
            );
            domainEntity.setHouseholdMemberAmount(
                    new HouseholdMemberAmountVO(jpaEntity.getMemberAmount())
            );
            List<ProducerEntity> producers = new ArrayList<>();
            for (Producer producer : jpaEntity.getProducers()) {
                producers.add(this.toDomain(producer));
            }
            domainEntity.setProducers(
                    producers
            );
            List<StorageEntity> storages = new ArrayList<>();
            for (Storage storage : jpaEntity.getStorages()) {
                storages.add(this.toDomain(storage));
            }
            domainEntity.setStorages(
                    storages
            );
            List<ConsumerEntity> consumers = new ArrayList<>();
            for (Consumer storage : jpaEntity.getConsumers()) {
                consumers.add(this.toDomain(storage));
            }
            domainEntity.setConsumers(
                    consumers
            );
            return domainEntity;
        } catch (StorageException | ConsumerException | ProducerException e) {
            throw new HouseholdException(e.getMessage(), e);
        }
    }

    public ProducerEntity toDomain(Producer jpaEntity) throws ProducerException {
        ProducerEntity domainEntity = new ProducerEntity();
        domainEntity.setProducerId(
                new ProducerIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setProducerPower(
                new ProducerPowerVO(jpaEntity.getProducerPower().getRatedPower())
        );
        domainEntity.setProducerType(
                new ProducerTypeVO(
                        jpaEntity.getProducerType().getProductType(),
                        jpaEntity.getProducerType().getEnergyType()
                )
        );
        domainEntity.setProducerStatus(
                new ProducerStatusVO(
                        jpaEntity.getProducerStatus().isRunning(),
                        jpaEntity.getProducerStatus().getCapacity()
                )
        );
        return domainEntity;
    }

    public StorageEntity toDomain(Storage jpaEntity) throws StorageException {
        StorageEntity domainEntity = new StorageEntity();
        domainEntity.setStorageId(
                new StorageIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setStoragePower(
                new StoragePowerVO(jpaEntity.getStoragePower().getRatedPower())
        );
        domainEntity.setStorageType(
                new StorageTypeVO(
                        jpaEntity.getStorageType().getEnergyType()
                )
        );
        domainEntity.setStorageStatus(new StorageStatusVO(jpaEntity.getStorageStatus().getCapacity()));
        return domainEntity;
    }

    public ConsumerEntity toDomain(Consumer jpaEntity) throws ConsumerException {
        ConsumerEntity domainEntity = new ConsumerEntity();
        domainEntity.setConsumerId(
                new ConsumerIdVO(jpaEntity.getBusinessKey())
        );
        domainEntity.setConsumerPower(
                new ConsumerPowerVO(jpaEntity.getConsumerPower().getConsumingPower())
        );
        domainEntity.setConsumerStatus(
                new ConsumerStatusVO(jpaEntity.getConsumerStatus().isRunning())
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
        if (domainEntity.getHouseholds() != null && !domainEntity.getHouseholds().isEmpty()) {
            jpaEntity.setHouseholds(
                    domainEntity.getHouseholds().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        jpaEntity.setPublished(domainEntity.getPublished().isPublished());
        return jpaEntity;
    }

    public DecentralizedPowerPlant toInfrastructure(DecentralizedPowerPlantAggregate domainEntity) {
        DecentralizedPowerPlant jpaEntity = new DecentralizedPowerPlant();
        jpaEntity.setBusinessKey(domainEntity.getDecentralizedPowerPlantId().getId());
        if (domainEntity.getProducers() != null && !domainEntity.getProducers().isEmpty()) {
            jpaEntity.setProducers(
                    domainEntity.getProducers().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getStorages() != null && !domainEntity.getStorages().isEmpty()) {
            jpaEntity.setStorages(
                    domainEntity.getStorages().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        return jpaEntity;
    }

    public Household toInfrastructure(HouseholdAggregate domainEntity) {
        Household jpaEntity = new Household();
        jpaEntity.setBusinessKey(domainEntity.getHouseholdId().getId());
        jpaEntity.setMemberAmount(domainEntity.getHouseholdMemberAmount().getAmount());
        if (domainEntity.getProducers() != null && !domainEntity.getProducers().isEmpty()) {
            jpaEntity.setProducers(
                    domainEntity.getProducers().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getStorages() != null && !domainEntity.getStorages().isEmpty()) {
            jpaEntity.setStorages(
                    domainEntity.getStorages().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getConsumers() != null && !domainEntity.getConsumers().isEmpty()) {
            jpaEntity.setConsumers(
                    domainEntity.getConsumers().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        return jpaEntity;
    }

    public Producer toInfrastructure(ProducerEntity domainEntity) {
        Producer jpaEntity = new Producer();
        jpaEntity.setBusinessKey(domainEntity.getProducerId().getId());
        ProducerPower producerPower = new ProducerPower();
        producerPower.setRatedPower(domainEntity.getProducerPower().getRatedPower());
        jpaEntity.setProducerPower(
                producerPower
        );
        ProducerType producerType = new ProducerType();
        producerType.setProductType(domainEntity.getProducerType().getProductType().toString());
        producerType.setEnergyType(domainEntity.getProducerType().getEnergyType().toString());
        jpaEntity.setProducerType(producerType);
        ProducerStatus status = new ProducerStatus();
        status.setRunning(domainEntity.getProducerStatus().isRunning());
        status.setCapacity(domainEntity.getProducerStatus().getCapacity());
        jpaEntity.setProducerStatus(status);
        return jpaEntity;
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
        StorageStatus storageStatus = new StorageStatus();
        storageStatus.setCapacity(domainEntity.getStorageStatus().getCapacity());
        jpaEntity.setStorageStatus(storageStatus);
        return jpaEntity;
    }

    public Consumer toInfrastructure(ConsumerEntity domainEntity) {
        Consumer jpaEntity = new Consumer();
        jpaEntity.setBusinessKey(domainEntity.getConsumerId().getId());
        ConsumerPower consumerPower = new ConsumerPower();
        consumerPower.setConsumingPower(domainEntity.getConsumerPower().getConsumingPower());
        jpaEntity.setConsumerPower(
                consumerPower
        );
        ConsumerStatus status = new ConsumerStatus();
        status.setRunning(domainEntity.getConsumerStatus().isRunning());
        jpaEntity.setConsumerStatus(status);
        return jpaEntity;
    }
}