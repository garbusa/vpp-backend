package de.uol.vpp.masterdata.application;

import de.uol.vpp.masterdata.application.dto.*;
import de.uol.vpp.masterdata.domain.EnergyType;
import de.uol.vpp.masterdata.domain.ProductType;
import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.valueobjects.*;
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

    public HouseholdDTO toApplication(HouseholdAggregate domainEntity) {
        HouseholdDTO dto = new HouseholdDTO();
        dto.setHouseholdId(domainEntity.getHouseholdId().getId());
        dto.setHouseholdMemberAmount(domainEntity.getHouseholdMemberAmount().getAmount());
//        dto.setConsumers(null);
//        dto.setProducers(null);
//        dto.setStorages(null);
        return dto;
    }

    public HouseholdAggregate toDomain(HouseholdDTO dto) {
        HouseholdAggregate domainEntity = new HouseholdAggregate();
        domainEntity.setHouseholdId(new HouseholdIdVO(dto.getHouseholdId()));
        domainEntity.setHouseholdMemberAmount(new HouseholdMemberAmountVO(dto.getHouseholdMemberAmount()));
//        domainEntity.setConsumers(null);
//        domainEntity.setProducers(null);
//        domainEntity.setStorages(null);
        return domainEntity;
    }

    public ProducerDTO toApplication(ProducerEntity domainEntity) {
        ProducerDTO dto = new ProducerDTO();
        dto.setProducerId(domainEntity.getProducerId().getId());
        dto.setEnergyType(domainEntity.getProducerType().getEnergyType().toString());
        dto.setProductType(domainEntity.getProducerType().getProductType().toString());
        dto.setRatedPower(domainEntity.getProducerPower().getRatedPower());
        dto.setHeight(domainEntity.getProducerDimension().getHeight());
        dto.setLength(domainEntity.getProducerDimension().getLength());
        dto.setWidth(domainEntity.getProducerDimension().getWidth());
        dto.setWeight(domainEntity.getProducerDimension().getWeight());
        return dto;
    }

    public ProducerEntity toDomain(ProducerDTO dto) {
        ProducerEntity domainEntity = new ProducerEntity();
        domainEntity.setProducerId(new ProducerIdVO(dto.getProducerId()));
        domainEntity.setProducerType(
                new ProducerTypeVO(
                        ProductType.valueOf(dto.getProductType()),
                        EnergyType.valueOf(dto.getEnergyType())
                )
        );
        domainEntity.setProducerPower(
                new ProducerPowerVO(dto.getRatedPower())
        );
        domainEntity.setProducerDimension(
                new ProducerDimensionVO(
                        dto.getHeight(),
                        dto.getWidth(),
                        dto.getLength(),
                        dto.getWeight()
                )
        );
        return domainEntity;
    }

    public ConsumerDTO toApplication(ConsumerEntity domainEntity) {
        ConsumerDTO dto = new ConsumerDTO();
        dto.setConsumerId(domainEntity.getConsumerId().getId());
        dto.setConsumingPower(domainEntity.getConsumerPower().getConsumingPower());
        return dto;
    }

    public ConsumerEntity toDomain(ConsumerDTO dto) {
        ConsumerEntity domainEntity = new ConsumerEntity();
        domainEntity.setConsumerId(
                new ConsumerIdVO(dto.getConsumerId())
        );
        domainEntity.setConsumerPower(
                new ConsumerPowerVO(dto.getConsumingPower())
        );
        return domainEntity;
    }

    public StorageDTO toApplication(StorageEntity domainEntity) {
        StorageDTO dto = new StorageDTO();
        dto.setStorageId(domainEntity.getStorageId().getId());
        dto.setRatedPower(domainEntity.getStoragePower().getRatedPower());
        dto.setEnergyType(domainEntity.getStorageType().getEnergyType().toString());
        return dto;
    }

    public StorageEntity toDomain(StorageDTO dto) {
        StorageEntity domainEntity = new StorageEntity();
        domainEntity.setStorageId(
                new StorageIdVO(dto.getStorageId())
        );
        domainEntity.setStoragePower(
                new StoragePowerVO(dto.getRatedPower())
        );
        domainEntity.setStorageType(
                new StorageTypeVO(EnergyType.valueOf(
                        dto.getEnergyType()
                ))
        );
        return domainEntity;
    }


}
