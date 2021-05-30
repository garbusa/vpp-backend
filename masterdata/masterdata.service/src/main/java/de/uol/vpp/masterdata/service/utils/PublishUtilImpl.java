package de.uol.vpp.masterdata.service.utils;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.*;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.repositories.*;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Die Methoden dieser Klasse prüfen die Editierbarkeit der einzelnen Komponenten eines virtuellen Kraftwerks.
 * Ein VK ist nur dann editierbar, wenn es nicht veröffentlicht ist.
 */
@Service
@RequiredArgsConstructor
public class PublishUtilImpl implements IPublishUtil {

    private static final String DOES_NOT_BELONG_PREFIX = "Überprüfung des 'veröffentlicht'-Status ist fehlgeschlagen, da ";
    private static final String DOES_NOT_BELONG_POSTFIX = " nicht dem VK angehört";
    private static final String DOES_NOT_EXIST = "Überprüfung des 'veröffentlicht'-Status ist fehlgeschlagen, da VK nicht existiert";
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IStorageRepository storageRepository;
    private final IWaterEnergyRepository waterEnergyRepository;
    private final IWindEnergyRepository windEnergyRepository;
    private final ISolarEnergyRepository solarEnergyRepository;
    private final IOtherEnergyRepository otherEnergyRepository;

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, DecentralizedPowerPlantIdVO decentralizedPowerPlantId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();

                if (decentralizedPowerPlantRepository.getById(decentralizedPowerPlantId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasDpp = false;
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    if (dpp.getDecentralizedPowerPlantId().getValue().equals(decentralizedPowerPlantId.getValue())) {
                        hasDpp = true;
                    }
                }
                if (hasDpp) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "DK" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | DecentralizedPowerPlantRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, HouseholdIdVO householdId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                //Check if vpp has dpp
                VirtualPowerPlantAggregate vpp = vppOptional.get();


                if (householdRepository.getById(householdId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasHousehold = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    if (household.getHouseholdId().getValue().equals(householdId.getValue())) {
                        hasHousehold = true;
                    }
                }
                if (hasHousehold) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "Haushalt" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | HouseholdRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, SolarEnergyIdVO solarEnergyId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();

                if (solarEnergyRepository.getById(solarEnergyId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasSolar = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (SolarEnergyEntity producer : household.getSolars()) {
                        if (producer.getId().getValue().equals(solarEnergyId.getValue())) {
                            hasSolar = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (SolarEnergyEntity producer : dpp.getSolars()) {
                        if (producer.getId().getValue().equals(solarEnergyId.getValue())) {
                            hasSolar = true;
                        }
                    }
                }
                if (hasSolar) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "Solarenergieanlage" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | ProducerRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, OtherEnergyIdVO otherEnergyId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();


                if (otherEnergyRepository.getById(otherEnergyId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasOther = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (OtherEnergyEntity producer : household.getOthers()) {
                        if (producer.getId().getValue().equals(otherEnergyId.getValue())) {
                            hasOther = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (OtherEnergyEntity producer : dpp.getOthers()) {
                        if (producer.getId().getValue().equals(otherEnergyId.getValue())) {
                            hasOther = true;
                        }
                    }
                }
                if (hasOther) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "alternative Erzeugungsanlage" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | ProducerRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }


    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, WindEnergyIdVO windEnergyId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();


                if (windEnergyRepository.getById(windEnergyId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasWind = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (WindEnergyEntity producer : household.getWinds()) {
                        if (producer.getId().getValue().equals(windEnergyId.getValue())) {
                            hasWind = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (WindEnergyEntity producer : dpp.getWinds()) {
                        if (producer.getId().getValue().equals(windEnergyId.getValue())) {
                            hasWind = true;
                        }
                    }
                }
                if (hasWind) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "Windkraftanlage" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | ProducerRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }


    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, WaterEnergyIdVO waterEnergyId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();

                if (waterEnergyRepository.getById(waterEnergyId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasWater = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (WaterEnergyEntity producer : household.getWaters()) {
                        if (producer.getId().getValue().equals(waterEnergyId.getValue())) {
                            hasWater = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (WaterEnergyEntity producer : dpp.getWaters()) {
                        if (producer.getId().getValue().equals(waterEnergyId.getValue())) {
                            hasWater = true;
                        }
                    }
                }
                if (hasWater) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "Wasserkraftanlage" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | ProducerRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }


    @Override
    public boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, StorageIdVO storageId) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(virtualPowerPlantId);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();


                if (storageRepository.getById(storageId).isEmpty()) {
                    return !vpp.getPublished().isValue();
                }

                boolean hasStorage = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (StorageEntity storage : household.getStorages()) {
                        if (storage.getStorageId().getValue().equals(storageId.getValue())) {
                            hasStorage = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (StorageEntity storage : dpp.getStorages()) {
                        if (storage.getStorageId().getValue().equals(storageId.getValue())) {
                            hasStorage = true;
                        }
                    }
                }
                if (hasStorage) {
                    return !vpp.getPublished().isValue();
                } else {
                    throw new PublishException(DOES_NOT_BELONG_PREFIX + "Speicher" + DOES_NOT_BELONG_POSTFIX);
                }
            } else {
                throw new PublishException(DOES_NOT_EXIST);
            }
        } catch (VirtualPowerPlantRepositoryException | StorageRepositoryException e) {
            throw new PublishException(e.getMessage());
        }
    }

}
