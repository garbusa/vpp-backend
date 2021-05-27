package de.uol.vpp.masterdata.infrastructure;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.*;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import de.uol.vpp.masterdata.infrastructure.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Konvertierungsklasse zwischen der Infrastruktur und Domänenschicht für die Kommunikation mit der Serviceschicht
 */
@Service
@RequiredArgsConstructor
public class InfrastructureEntityConverter {

    public VirtualPowerPlantAggregate toDomain(VirtualPowerPlant jpaEntity) throws VirtualPowerPlantException {
        try {
            VirtualPowerPlantAggregate domainEntity = new VirtualPowerPlantAggregate();
            domainEntity.setVirtualPowerPlantId(
                    new VirtualPowerPlantIdVO(jpaEntity.getId())
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
                    new DecentralizedPowerPlantIdVO(jpaEntity.getId())
            );
            List<WaterEnergyEntity> waters = new ArrayList<>();
            for (WaterEnergy water : jpaEntity.getWaters()) {
                waters.add(this.toDomain(water));
            }
            domainEntity.setWaters(
                    waters
            );

            List<WindEnergyEntity> winds = new ArrayList<>();
            for (WindEnergy wind : jpaEntity.getWinds()) {
                winds.add(this.toDomain(wind));
            }
            domainEntity.setWinds(
                    winds
            );

            List<SolarEnergyEntity> solars = new ArrayList<>();
            for (SolarEnergy solar : jpaEntity.getSolars()) {
                solars.add(this.toDomain(solar));
            }
            domainEntity.setSolars(
                    solars
            );

            List<OtherEnergyEntity> others = new ArrayList<>();
            for (OtherEnergy other : jpaEntity.getOthers()) {
                others.add(this.toDomain(other));
            }
            domainEntity.setOthers(
                    others
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
                    new HouseholdIdVO(jpaEntity.getId())
            );
            domainEntity.setHouseholdMemberAmount(
                    new HouseholdMemberAmountVO(jpaEntity.getMemberAmount())
            );
            List<WaterEnergyEntity> waters = new ArrayList<>();
            for (WaterEnergy water : jpaEntity.getWaters()) {
                waters.add(this.toDomain(water));
            }
            domainEntity.setWaters(
                    waters
            );

            List<WindEnergyEntity> winds = new ArrayList<>();
            for (WindEnergy wind : jpaEntity.getWinds()) {
                winds.add(this.toDomain(wind));
            }
            domainEntity.setWinds(
                    winds
            );

            List<SolarEnergyEntity> solars = new ArrayList<>();
            for (SolarEnergy solar : jpaEntity.getSolars()) {
                solars.add(this.toDomain(solar));
            }
            domainEntity.setSolars(
                    solars
            );
            List<OtherEnergyEntity> others = new ArrayList<>();
            for (OtherEnergy other : jpaEntity.getOthers()) {
                others.add(this.toDomain(other));
            }
            domainEntity.setOthers(
                    others
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
            throw new HouseholdException(e.getMessage(), e);
        }
    }

    public WaterEnergyEntity toDomain(WaterEnergy jpaEntity) throws ProducerException {
        WaterEnergyEntity domainEntity = new WaterEnergyEntity();
        domainEntity.setId(new WaterEnergyIdVO(jpaEntity.getId()));
        domainEntity.setCapacity(new WaterEnergyCapacityVO(jpaEntity.getCapacity()));
        domainEntity.setEfficiency(new WaterEnergyEfficiencyVO(jpaEntity.getEfficiency()));
        domainEntity.setHeight(new WaterEnergyHeightVO(jpaEntity.getHeight()));
        domainEntity.setDensity(new WaterEnergyDensityVO(jpaEntity.getDensity()));
        domainEntity.setGravity(new WaterEnergyGravityVO(jpaEntity.getGravity()));
        domainEntity.setVolumeFlow(new WaterEnergyVolumeFlowVO(jpaEntity.getVolumeFlow()));
        return domainEntity;
    }

    public WindEnergyEntity toDomain(WindEnergy jpaEntity) throws ProducerException {
        WindEnergyEntity domainEntity = new WindEnergyEntity();
        domainEntity.setId(new WindEnergyIdVO(jpaEntity.getId()));
        domainEntity.setLatitude(new WindEnergyLatitudeVO(jpaEntity.getLatitude()));
        domainEntity.setLongitude(new WindEnergyLongitudeVO(jpaEntity.getLongitude()));
        domainEntity.setCapacity(new WindEnergyCapacityVO(jpaEntity.getCapacity()));
        domainEntity.setEfficiency(new WindEnergyEfficiencyVO(jpaEntity.getEfficiency()));
        domainEntity.setHeight(new WindEnergyHeightVO(jpaEntity.getHeight()));
        domainEntity.setRadius(new WindEnergyRadiusVO(jpaEntity.getRadius()));
        return domainEntity;
    }

    public SolarEnergyEntity toDomain(SolarEnergy jpaEntity) throws ProducerException {
        SolarEnergyEntity domainEntity = new SolarEnergyEntity();
        domainEntity.setId(new SolarEnergyIdVO(jpaEntity.getId()));
        domainEntity.setLatitude(new SolarEnergyLatitudeVO(jpaEntity.getLatitude()));
        domainEntity.setLongitude(new SolarEnergyLongitudeVO(jpaEntity.getLongitude()));
        domainEntity.setCapacity(new SolarEnergyCapacityVO(jpaEntity.getCapacity()));
        domainEntity.setRatedCapacity(new SolarEnergyRatedCapacityVO(jpaEntity.getRatedCapacity()));
        domainEntity.setAlignment(new SolarEnergyAlignmentVO(jpaEntity.getAlignment()));
        domainEntity.setSlope(new SolarEnergySlopeVO(jpaEntity.getSlope()));
        return domainEntity;
    }

    public OtherEnergyEntity toDomain(OtherEnergy jpaEntity) throws ProducerException {
        OtherEnergyEntity domainEntity = new OtherEnergyEntity();
        domainEntity.setId(new OtherEnergyIdVO(jpaEntity.getId()));
        domainEntity.setCapacity(new OtherEnergyCapacityVO(jpaEntity.getCapacity()));
        domainEntity.setRatedCapacity(new OtherEnergyRatedCapacityVO(jpaEntity.getRatedCapacity()));
        return domainEntity;
    }

    public StorageEntity toDomain(Storage jpaEntity) throws StorageException {
        StorageEntity domainEntity = new StorageEntity();
        domainEntity.setStorageId(
                new StorageIdVO(jpaEntity.getId())
        );
        domainEntity.setStoragePower(
                new StoragePowerVO(jpaEntity.getRatedPower())
        );
        domainEntity.setLoadTimeHour(
                new StorageLoadTimeHourVO(
                        jpaEntity.getLoadTimeHour()
                )
        );
        domainEntity.setStorageCapacity(new StorageCapacityVO(jpaEntity.getCapacity()));
        return domainEntity;
    }

    public VirtualPowerPlant toInfrastructure(VirtualPowerPlantAggregate domainEntity) {
        VirtualPowerPlant jpaEntity = new VirtualPowerPlant();
        jpaEntity.setId(domainEntity.getVirtualPowerPlantId().getValue());
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
        jpaEntity.setPublished(domainEntity.getPublished().isValue());
        return jpaEntity;
    }

    public DecentralizedPowerPlant toInfrastructure(DecentralizedPowerPlantAggregate domainEntity) {
        DecentralizedPowerPlant jpaEntity = new DecentralizedPowerPlant();
        jpaEntity.setId(domainEntity.getDecentralizedPowerPlantId().getValue());
        if (domainEntity.getSolars() != null && !domainEntity.getSolars().isEmpty()) {
            jpaEntity.setSolars(
                    domainEntity.getSolars().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getWinds() != null && !domainEntity.getWinds().isEmpty()) {
            jpaEntity.setWinds(
                    domainEntity.getWinds().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getWaters() != null && !domainEntity.getWaters().isEmpty()) {
            jpaEntity.setWaters(
                    domainEntity.getWaters().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getOthers() != null && !domainEntity.getOthers().isEmpty()) {
            jpaEntity.setOthers(
                    domainEntity.getOthers().stream().map(this::toInfrastructure)
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
        jpaEntity.setId(domainEntity.getHouseholdId().getValue());
        jpaEntity.setMemberAmount(domainEntity.getHouseholdMemberAmount().getValue());
        if (domainEntity.getSolars() != null && !domainEntity.getSolars().isEmpty()) {
            jpaEntity.setSolars(
                    domainEntity.getSolars().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getWinds() != null && !domainEntity.getWinds().isEmpty()) {
            jpaEntity.setWinds(
                    domainEntity.getWinds().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getWaters() != null && !domainEntity.getWaters().isEmpty()) {
            jpaEntity.setWaters(
                    domainEntity.getWaters().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        }
        if (domainEntity.getOthers() != null && !domainEntity.getOthers().isEmpty()) {
            jpaEntity.setOthers(
                    domainEntity.getOthers().stream().map(this::toInfrastructure)
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

    public SolarEnergy toInfrastructure(SolarEnergyEntity domainEntity) {
        SolarEnergy jpaEntity = new SolarEnergy();
        jpaEntity.setId(domainEntity.getId().getValue());
        jpaEntity.setCapacity(domainEntity.getCapacity().getValue());
        jpaEntity.setRatedCapacity(domainEntity.getRatedCapacity().getValue());
        jpaEntity.setLatitude(domainEntity.getLatitude().getValue());
        jpaEntity.setLongitude(domainEntity.getLongitude().getValue());
        jpaEntity.setAlignment(domainEntity.getAlignment().getValue());
        jpaEntity.setSlope(domainEntity.getSlope().getValue());
        return jpaEntity;
    }

    public WindEnergy toInfrastructure(WindEnergyEntity domainEntity) {
        WindEnergy jpaEntity = new WindEnergy();
        jpaEntity.setId(domainEntity.getId().getValue());
        jpaEntity.setCapacity(domainEntity.getCapacity().getValue());
        jpaEntity.setEfficiency(domainEntity.getEfficiency().getValue());
        jpaEntity.setLatitude(domainEntity.getLatitude().getValue());
        jpaEntity.setLongitude(domainEntity.getLongitude().getValue());
        jpaEntity.setRadius(domainEntity.getRadius().getValue());
        jpaEntity.setHeight(domainEntity.getHeight().getValue());
        return jpaEntity;
    }

    public WaterEnergy toInfrastructure(WaterEnergyEntity domainEntity) {
        WaterEnergy jpaEntity = new WaterEnergy();
        jpaEntity.setId(domainEntity.getId().getValue());
        jpaEntity.setCapacity(domainEntity.getCapacity().getValue());
        jpaEntity.setDensity(domainEntity.getDensity().getValue());
        jpaEntity.setEfficiency(domainEntity.getEfficiency().getValue());
        jpaEntity.setGravity(domainEntity.getGravity().getValue());
        jpaEntity.setHeight(domainEntity.getHeight().getValue());
        jpaEntity.setVolumeFlow(domainEntity.getVolumeFlow().getValue());
        return jpaEntity;
    }

    public OtherEnergy toInfrastructure(OtherEnergyEntity domainEntity) {
        OtherEnergy jpaEntity = new OtherEnergy();
        jpaEntity.setId(domainEntity.getId().getValue());
        jpaEntity.setCapacity(domainEntity.getCapacity().getValue());
        jpaEntity.setRatedCapacity(domainEntity.getRatedCapacity().getValue());
        return jpaEntity;
    }

    public Storage toInfrastructure(StorageEntity domainEntity) {
        Storage jpaEntity = new Storage();
        jpaEntity.setId(domainEntity.getStorageId().getValue());
        jpaEntity.setRatedPower(domainEntity.getStoragePower().getValue());
        jpaEntity.setLoadTimeHour(domainEntity.getLoadTimeHour().getValue());
        jpaEntity.setCapacity(domainEntity.getStorageCapacity().getValue());
        return jpaEntity;
    }


}