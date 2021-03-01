package de.uol.vpp.masterdata.application;

import de.uol.vpp.masterdata.application.dto.*;
import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationEntityConverter {

    public VirtualPowerPlantDTO toApplication(VirtualPowerPlantAggregate domainEntity) {
        VirtualPowerPlantDTO dto = new VirtualPowerPlantDTO();
        dto.setVirtualPowerPlantId(domainEntity.getVirtualPowerPlantId().getId());
        dto.setDecentralizedPowerPlants(domainEntity.getDecentralizedPowerPlants().stream()
                .map(this::toApplication).collect(Collectors.toList()));
        dto.setPublished(domainEntity.getPublished().isPublished());
        return dto;
    }

    public DecentralizedPowerPlantDTO toApplication(DecentralizedPowerPlantAggregate domainEntity) {
        DecentralizedPowerPlantDTO dto = new DecentralizedPowerPlantDTO();
        dto.setDecentralizedPowerPlantId(domainEntity.getDecentralizedPowerPlantId().getId());
        dto.setProducers(domainEntity.getProducers().stream().map(this::toApplication).collect(Collectors.toList()));
        return dto;
    }

    public ProducerDTO toApplication(ProducerEntity domainEntity) {
        ProducerDTO dto = new ProducerDTO();
        dto.setProducerId(domainEntity.getProducerId().getId());
        dto.setEnergyType(domainEntity.getProducerType().getEnergyType().toString());
        dto.setProductType(domainEntity.getProducerType().getProductType().toString());
        dto.setRunning(domainEntity.getProducerStatus().isRunning());
        dto.setCapacity(domainEntity.getProducerStatus().getCapacity());
        dto.setRatedPower(domainEntity.getProducerPower().getRatedPower());
        return dto;
    }

    public VirtualPowerPlantAggregate toDomain(VirtualPowerPlantDTO dto) throws VirtualPowerPlantException {
        try {
            VirtualPowerPlantAggregate domainEntity = new VirtualPowerPlantAggregate();
            domainEntity.setVirtualPowerPlantId(new VirtualPowerPlantIdVO(dto.getVirtualPowerPlantId()));
            domainEntity.setPublished(new VirtualPowerPlantPublishedVO(dto.isPublished()));
            List<DecentralizedPowerPlantAggregate> dpps = new ArrayList<>();
            for (DecentralizedPowerPlantDTO dpp : dto.getDecentralizedPowerPlants()) {
                dpps.add(this.toDomain(dpp));
            }
            domainEntity.setDecentralizedPowerPlants(dpps);
            List<HouseholdAggregate> households = new ArrayList<>();
            for (HouseholdDTO household : dto.getHouseholds()) {
                households.add(this.toDomain(household));
            }
            domainEntity.setHouseholds(households);
            return domainEntity;
        } catch (HouseholdException | DecentralizedPowerPlantException e) {
            throw new VirtualPowerPlantException(e.getMessage(), e);
        }

    }

    public DecentralizedPowerPlantAggregate toDomain(DecentralizedPowerPlantDTO dto) throws DecentralizedPowerPlantException {
        try {
            DecentralizedPowerPlantAggregate domainEntity = new DecentralizedPowerPlantAggregate();
            domainEntity.setDecentralizedPowerPlantId(new DecentralizedPowerPlantIdVO(dto.getDecentralizedPowerPlantId()));
            List<ProducerEntity> producers = new ArrayList<>();
            for (ProducerDTO producer : dto.getProducers()) {
                producers.add(this.toDomain(producer));
            }
            domainEntity.setProducers(producers);
            List<StorageEntity> storages = new ArrayList<>();
            for (StorageDTO producer : dto.getStorages()) {
                storages.add(this.toDomain(producer));
            }
            domainEntity.setStorages(storages);
            return domainEntity;
        } catch (StorageException | ProducerException e) {
            throw new DecentralizedPowerPlantException(e.getMessage(), e);
        }

    }

    public HouseholdAggregate toDomain(HouseholdDTO dto) throws HouseholdException {
        try {
            HouseholdAggregate domainEntity = new HouseholdAggregate();
            domainEntity.setHouseholdId(new HouseholdIdVO(dto.getHouseholdId()));
            domainEntity.setHouseholdMemberAmount(new HouseholdMemberAmountVO(dto.getHouseholdMemberAmount()));
            List<ProducerEntity> producers = new ArrayList<>();
            for (ProducerDTO producer : dto.getProducers()) {
                producers.add(this.toDomain(producer));
            }
            domainEntity.setProducers(producers);
            List<StorageEntity> storages = new ArrayList<>();
            for (StorageDTO producer : dto.getStorages()) {
                storages.add(this.toDomain(producer));
            }
            domainEntity.setStorages(storages);
            List<ConsumerEntity> consumers = new ArrayList<>();
            for (ConsumerDTO consumer : dto.getConsumers()) {
                consumers.add(this.toDomain(consumer));
            }
            domainEntity.setConsumers(consumers);
            return domainEntity;
        } catch (ProducerException | ConsumerException | StorageException e) {
            throw new HouseholdException(e.getMessage(), e);
        }
    }

    public ProducerEntity toDomain(ProducerDTO dto) throws ProducerException {
        ProducerEntity domainEntity = new ProducerEntity();
        domainEntity.setProducerId(new ProducerIdVO(dto.getProducerId()));
        domainEntity.setProducerType(
                new ProducerTypeVO(
                        dto.getProductType(),
                        dto.getEnergyType()
                )
        );
        domainEntity.setProducerPower(
                new ProducerPowerVO(dto.getRatedPower())
        );
        domainEntity.setProducerStatus(
                new ProducerStatusVO(dto.isRunning(), dto.getCapacity())
        );
        return domainEntity;
    }

    public StorageEntity toDomain(StorageDTO dto) throws StorageException {
        StorageEntity domainEntity = new StorageEntity();
        domainEntity.setStorageId(
                new StorageIdVO(dto.getStorageId())
        );
        domainEntity.setStoragePower(
                new StoragePowerVO(dto.getRatedPower())
        );
        domainEntity.setStorageType(
                new StorageTypeVO(
                        dto.getEnergyType()
                )
        );
        domainEntity.setStorageStatus(
                new StorageStatusVO(dto.getCapacity())
        );
        return domainEntity;
    }

    public ConsumerEntity toDomain(ConsumerDTO dto) throws ConsumerException {
        ConsumerEntity domainEntity = new ConsumerEntity();
        domainEntity.setConsumerId(
                new ConsumerIdVO(dto.getConsumerId())
        );
        domainEntity.setConsumerPower(
                new ConsumerPowerVO(dto.getConsumingPower())
        );
        domainEntity.setConsumerStatus(
                new ConsumerStatusVO(dto.isRunning())
        );
        return domainEntity;
    }

    public HouseholdDTO toApplication(HouseholdAggregate domainEntity) {
        HouseholdDTO dto = new HouseholdDTO();
        dto.setHouseholdId(domainEntity.getHouseholdId().getId());
        dto.setHouseholdMemberAmount(domainEntity.getHouseholdMemberAmount().getAmount());
        dto.setStorages(domainEntity.getStorages().stream().map(this::toApplication).collect(Collectors.toList()));
        dto.setProducers(domainEntity.getProducers().stream().map(this::toApplication).collect(Collectors.toList()));
        dto.setConsumers(domainEntity.getConsumers().stream().map(this::toApplication).collect(Collectors.toList()));
        return dto;
    }

    public StorageDTO toApplication(StorageEntity domainEntity) {
        StorageDTO dto = new StorageDTO();
        dto.setStorageId(domainEntity.getStorageId().getId());
        dto.setRatedPower(domainEntity.getStoragePower().getRatedPower());
        dto.setEnergyType(domainEntity.getStorageType().getEnergyType().toString());
        dto.setCapacity(domainEntity.getStorageStatus().getCapacity());
        return dto;
    }

    public ConsumerDTO toApplication(ConsumerEntity domainEntity) {
        ConsumerDTO dto = new ConsumerDTO();
        dto.setConsumerId(domainEntity.getConsumerId().getId());
        dto.setConsumingPower(domainEntity.getConsumerPower().getConsumingPower());
        dto.setRunning(domainEntity.getConsumerStatus().isRunning());
        return dto;
    }


}
