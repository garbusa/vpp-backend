package de.uol.vpp.masterdata.service.utils;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.*;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import de.uol.vpp.masterdata.infrastructure.repositories.VirtualPowerPlantRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublishUtilImpl implements IPublishUtil {

    private final VirtualPowerPlantRepositoryImpl virtualPowerPlantRepository;

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, DecentralizedPowerPlantIdVO dppBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasDpp = false;
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    if (dpp.getDecentralizedPowerPlantId().getValue().equals(dppBusinessKey.getValue())) {
                        hasDpp = true;
                    }
                }
                if (hasDpp) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. dpp does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, HouseholdIdVO householdBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                //Check if vpp has dpp
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasHousehold = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    if (household.getHouseholdId().getValue().equals(householdBusinessKey.getValue())) {
                        hasHousehold = true;
                    }
                }
                if (hasHousehold) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. household does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, SolarEnergyIdVO solarEnergyBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasSolar = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (SolarEnergyEntity producer : household.getSolars()) {
                        if (producer.getId().getValue().equals(solarEnergyBusinessKey.getValue())) {
                            hasSolar = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (SolarEnergyEntity producer : dpp.getSolars()) {
                        if (producer.getId().getValue().equals(solarEnergyBusinessKey.getValue())) {
                            hasSolar = true;
                        }
                    }
                }
                if (hasSolar) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. producer does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, OtherEnergyIdVO otherEnergyBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasOther = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (OtherEnergyEntity producer : household.getOthers()) {
                        if (producer.getId().getValue().equals(otherEnergyBusinessKey.getValue())) {
                            hasOther = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (OtherEnergyEntity producer : dpp.getOthers()) {
                        if (producer.getId().getValue().equals(otherEnergyBusinessKey.getValue())) {
                            hasOther = true;
                        }
                    }
                }
                if (hasOther) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. producer does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }


    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, WindEnergyIdVO windEnergyBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasWind = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (WindEnergyEntity producer : household.getWinds()) {
                        if (producer.getId().getValue().equals(windEnergyBusinessKey.getValue())) {
                            hasWind = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (WindEnergyEntity producer : dpp.getWinds()) {
                        if (producer.getId().getValue().equals(windEnergyBusinessKey.getValue())) {
                            hasWind = true;
                        }
                    }
                }
                if (hasWind) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. producer does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }


    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, WaterEnergyIdVO waterEnergyBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasWater = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (WaterEnergyEntity producer : household.getWaters()) {
                        if (producer.getId().getValue().equals(waterEnergyBusinessKey.getValue())) {
                            hasWater = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (WaterEnergyEntity producer : dpp.getWaters()) {
                        if (producer.getId().getValue().equals(waterEnergyBusinessKey.getValue())) {
                            hasWater = true;
                        }
                    }
                }
                if (hasWater) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. producer does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }


    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, StorageIdVO storageBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasStorage = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (StorageEntity storage : household.getStorages()) {
                        if (storage.getStorageId().getValue().equals(storageBusinessKey.getValue())) {
                            hasStorage = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (StorageEntity storage : dpp.getStorages()) {
                        if (storage.getStorageId().getValue().equals(storageBusinessKey.getValue())) {
                            hasStorage = true;
                        }
                    }
                }
                if (hasStorage) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException("checking publish status failed. storage does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }

}
