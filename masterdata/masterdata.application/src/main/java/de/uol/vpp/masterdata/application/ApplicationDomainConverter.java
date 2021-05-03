package de.uol.vpp.masterdata.application;

import de.uol.vpp.masterdata.application.dto.*;
import de.uol.vpp.masterdata.application.dto.abstracts.DtoHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.abstracts.DomainHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.entities.*;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationDomainConverter {

    public VirtualPowerPlantDTO toApplication(VirtualPowerPlantAggregate domainEntity) {
        VirtualPowerPlantDTO dto = new VirtualPowerPlantDTO();
        dto.setVirtualPowerPlantId(domainEntity.getVirtualPowerPlantId().getValue());
        dto.setDecentralizedPowerPlants(domainEntity.getDecentralizedPowerPlants().stream()
                .map(this::toApplication).collect(Collectors.toList()));
        dto.setPublished(domainEntity.getPublished().isValue());
        dto.setShortageThreshold(domainEntity.getShortageThreshold().getValue());
        dto.setOverflowThreshold(domainEntity.getOverflowThreshold().getValue());
        return dto;
    }

    public DecentralizedPowerPlantDTO toApplication(DecentralizedPowerPlantAggregate domainEntity) {
        DecentralizedPowerPlantDTO dto = new DecentralizedPowerPlantDTO();
        dto.setDecentralizedPowerPlantId(domainEntity.getDecentralizedPowerPlantId().getValue());
        this.setProducersAndStoragesFromDomainToApplication(dto, domainEntity);
        return dto;
    }

    public SolarEnergyDTO toApplication(SolarEnergyEntity domainEntity) {
        SolarEnergyDTO dto = new SolarEnergyDTO();
        dto.setSolarEnergyId(domainEntity.getId().getValue());
        dto.setLatitude(domainEntity.getLatitude().getValue());
        dto.setLongitude(domainEntity.getLongitude().getValue());
        dto.setCapacity(domainEntity.getCapacity().getValue());
        dto.setRatedCapacity(domainEntity.getRatedCapacity().getValue());

        dto.setAlignment(domainEntity.getAlignment().getValue());
        dto.setSlope(domainEntity.getSlope().getValue());
        return dto;
    }

    public WaterEnergyDTO toApplication(WaterEnergyEntity domainEntity) {
        WaterEnergyDTO dto = new WaterEnergyDTO();
        dto.setWaterEnergyId(domainEntity.getId().getValue());
        dto.setCapacity(domainEntity.getCapacity().getValue());
        dto.setEfficiency(domainEntity.getEfficiency().getValue());

        dto.setDensity(domainEntity.getDensity().getValue());
        dto.setGravity(domainEntity.getGravity().getValue());
        dto.setHeight(domainEntity.getHeight().getValue());
        dto.setVolumeFlow(domainEntity.getVolumeFlow().getValue());
        return dto;
    }

    public WindEnergyDTO toApplication(WindEnergyEntity domainEntity) {
        WindEnergyDTO dto = new WindEnergyDTO();
        dto.setWindEnergyId(domainEntity.getId().getValue());
        dto.setLatitude(domainEntity.getLatitude().getValue());
        dto.setLongitude(domainEntity.getLongitude().getValue());
        dto.setCapacity(domainEntity.getCapacity().getValue());
        dto.setEfficiency(domainEntity.getEfficiency().getValue());

        dto.setHeight(domainEntity.getHeight().getValue());
        dto.setRadius(domainEntity.getRadius().getValue());
        return dto;
    }

    public OtherEnergyDTO toApplication(OtherEnergyEntity domainEntity) {
        OtherEnergyDTO dto = new OtherEnergyDTO();
        dto.setOtherEnergyId(domainEntity.getId().getValue());
        dto.setRatedCapacity(domainEntity.getRatedCapacity().getValue());
        dto.setCapacity(domainEntity.getCapacity().getValue());
        return dto;
    }

    public StorageDTO toApplication(StorageEntity domainEntity) {
        StorageDTO dto = new StorageDTO();
        dto.setStorageId(domainEntity.getStorageId().getValue());
        dto.setRatedPower(domainEntity.getStoragePower().getValue());
        dto.setLoadTimeHour(domainEntity.getLoadTimeHour().getValue());
        dto.setCapacity(domainEntity.getStorageCapacity().getValue());
        return dto;
    }

    public VirtualPowerPlantAggregate toDomain(VirtualPowerPlantDTO dto) throws VirtualPowerPlantException {
        try {
            VirtualPowerPlantAggregate domainEntity = new VirtualPowerPlantAggregate();
            domainEntity.setVirtualPowerPlantId(new VirtualPowerPlantIdVO(dto.getVirtualPowerPlantId()));
            domainEntity.setPublished(new VirtualPowerPlantPublishedVO(dto.isPublished()));
            domainEntity.setShortageThreshold(new VirtualPowerPlantShortageThresholdVO(dto.getShortageThreshold()));
            domainEntity.setOverflowThreshold(new VirtualPowerPlantOverflowThresholdVO(dto.getOverflowThreshold()));
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

    private void setProducersAndStoragesFromDomainToApplication(DtoHasProducersAndStorages dto, DomainHasProducersAndStorages domainEntity) {
        dto.setSolars(domainEntity.getSolars().stream().map(this::toApplication).collect(Collectors.toList()));
        dto.setWaters(domainEntity.getWaters().stream().map(this::toApplication).collect(Collectors.toList()));
        dto.setWinds(domainEntity.getWinds().stream().map(this::toApplication).collect(Collectors.toList()));
        dto.setOthers(domainEntity.getOthers().stream().map(this::toApplication).collect(Collectors.toList()));
        dto.setStorages(domainEntity.getStorages().stream().map(this::toApplication).collect(Collectors.toList()));
    }

    public DecentralizedPowerPlantAggregate toDomain(DecentralizedPowerPlantDTO dto) throws DecentralizedPowerPlantException {
        try {
            DecentralizedPowerPlantAggregate domainEntity = new DecentralizedPowerPlantAggregate();
            domainEntity.setDecentralizedPowerPlantId(new DecentralizedPowerPlantIdVO(dto.getDecentralizedPowerPlantId()));
            this.setProducersAndStoragesFromApplicationToDomain(dto, domainEntity);
            return domainEntity;
        } catch (StorageException | ProducerException e) {
            throw new DecentralizedPowerPlantException(e.getMessage(), e);
        }

    }

    private void setProducersAndStoragesFromApplicationToDomain(DtoHasProducersAndStorages dto, DomainHasProducersAndStorages domainEntity) throws ProducerException, StorageException {
        List<SolarEnergyEntity> solars = new ArrayList<>();
        for (SolarEnergyDTO producer : dto.getSolars()) {
            solars.add(this.toDomain(producer));
        }
        domainEntity.setSolars(solars);
        List<WindEnergyEntity> winds = new ArrayList<>();
        for (WindEnergyDTO producer : dto.getWinds()) {
            winds.add(this.toDomain(producer));
        }
        domainEntity.setWinds(winds);
        List<WaterEnergyEntity> waters = new ArrayList<>();
        for (WaterEnergyDTO producer : dto.getWaters()) {
            waters.add(this.toDomain(producer));
        }
        domainEntity.setWaters(waters);
        List<OtherEnergyEntity> others = new ArrayList<>();
        for (OtherEnergyDTO other : dto.getOthers()) {
            others.add(this.toDomain(other));
        }
        domainEntity.setOthers(others);

        List<StorageEntity> storages = new ArrayList<>();
        for (StorageDTO producer : dto.getStorages()) {
            storages.add(this.toDomain(producer));
        }
        domainEntity.setStorages(storages);
    }

    public SolarEnergyEntity toDomain(SolarEnergyDTO dto) throws ProducerException {
        SolarEnergyEntity domainEntity = new SolarEnergyEntity();
        domainEntity.setId(new SolarEnergyIdVO(dto.getSolarEnergyId()));
        domainEntity.setLongitude(new SolarEnergyLongitudeVO(dto.getLongitude()));
        domainEntity.setLatitude(new SolarEnergyLatitudeVO(dto.getLatitude()));
        domainEntity.setCapacity(new SolarEnergyCapacityVO(dto.getCapacity()));
        domainEntity.setRatedCapacity(new SolarEnergyRatedCapacityVO(dto.getRatedCapacity()));

        domainEntity.setAlignment(new SolarEnergyAlignmentVO(dto.getAlignment()));
        domainEntity.setSlope(new SolarEnergySlopeVO(dto.getSlope()));
        return domainEntity;
    }

    public WindEnergyEntity toDomain(WindEnergyDTO dto) throws ProducerException {
        WindEnergyEntity domainEntity = new WindEnergyEntity();
        domainEntity.setId(new WindEnergyIdVO(dto.getWindEnergyId()));
        domainEntity.setLongitude(new WindEnergyLongitudeVO(dto.getLongitude()));
        domainEntity.setLatitude(new WindEnergyLatitudeVO(dto.getLatitude()));
        domainEntity.setCapacity(new WindEnergyCapacityVO(dto.getCapacity()));
        domainEntity.setEfficiency(new WindEnergyEfficiencyVO(dto.getEfficiency()));

        domainEntity.setRadius(new WindEnergyRadiusVO(dto.getRadius()));
        domainEntity.setHeight(new WindEnergyHeightVO(dto.getHeight()));
        return domainEntity;
    }

    public WaterEnergyEntity toDomain(WaterEnergyDTO dto) throws ProducerException {
        WaterEnergyEntity domainEntity = new WaterEnergyEntity();
        domainEntity.setId(new WaterEnergyIdVO(dto.getWaterEnergyId()));
        domainEntity.setCapacity(new WaterEnergyCapacityVO(dto.getCapacity()));
        domainEntity.setEfficiency(new WaterEnergyEfficiencyVO(dto.getEfficiency()));

        domainEntity.setDensity(new WaterEnergyDensityVO(dto.getDensity()));
        domainEntity.setHeight(new WaterEnergyHeightVO(dto.getHeight()));
        domainEntity.setGravity(new WaterEnergyGravityVO(dto.getGravity()));
        domainEntity.setVolumeFlow(new WaterEnergyVolumeFlowVO(dto.getVolumeFlow()));
        return domainEntity;
    }

    public OtherEnergyEntity toDomain(OtherEnergyDTO dto) throws ProducerException {
        OtherEnergyEntity domainEntity = new OtherEnergyEntity();
        domainEntity.setId(new OtherEnergyIdVO(dto.getOtherEnergyId()));
        domainEntity.setRatedCapacity(new OtherEnergyRatedCapacityVO(dto.getRatedCapacity()));
        domainEntity.setCapacity(new OtherEnergyCapacityVO(dto.getCapacity()));
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
        domainEntity.setLoadTimeHour(
                new StorageLoadTimeHourVO(
                        dto.getLoadTimeHour()
                )
        );
        domainEntity.setStorageCapacity(
                new StorageCapacityVO(dto.getCapacity())
        );
        return domainEntity;
    }

    public HouseholdAggregate toDomain(HouseholdDTO dto) throws HouseholdException {
        try {
            HouseholdAggregate domainEntity = new HouseholdAggregate();
            domainEntity.setHouseholdId(new HouseholdIdVO(dto.getHouseholdId()));
            domainEntity.setHouseholdMemberAmount(new HouseholdMemberAmountVO(dto.getHouseholdMemberAmount()));
            this.setProducersAndStoragesFromApplicationToDomain(dto, domainEntity);
            return domainEntity;
        } catch (ProducerException | StorageException e) {
            throw new HouseholdException(e.getMessage(), e);
        }
    }

    public HouseholdDTO toApplication(HouseholdAggregate domainEntity) {
        HouseholdDTO dto = new HouseholdDTO();
        dto.setHouseholdId(domainEntity.getHouseholdId().getValue());
        dto.setHouseholdMemberAmount(domainEntity.getHouseholdMemberAmount().getValue());
        this.setProducersAndStoragesFromDomainToApplication(dto, domainEntity);
        return dto;
    }

}
