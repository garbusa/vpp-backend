package de.uol.vpp.masterdata.service.utils;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
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
                    if (dpp.getDecentralizedPowerPlantId().getId().equals(dppBusinessKey.getId())) {
                        hasDpp = true;
                    }
                }
                if (hasDpp) {
                    return !vpp.getPublished().isPublished();
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
                    if (household.getHouseholdId().getId().equals(householdBusinessKey.getId())) {
                        hasHousehold = true;
                    }
                }
                if (hasHousehold) {
                    return !vpp.getPublished().isPublished();
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
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, ProducerIdVO producerBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasProducer = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (ProducerEntity producer : household.getProducers()) {
                        if (producer.getProducerId().getId().equals(producerBusinessKey.getId())) {
                            hasProducer = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (ProducerEntity producer : dpp.getProducers()) {
                        if (producer.getProducerId().getId().equals(producerBusinessKey.getId())) {
                            hasProducer = true;
                        }
                    }
                }
                if (hasProducer) {
                    return !vpp.getPublished().isPublished();
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
                        if (storage.getStorageId().getId().equals(storageBusinessKey.getId())) {
                            hasStorage = true;
                        }
                    }
                }
                for (DecentralizedPowerPlantAggregate dpp : vpp.getDecentralizedPowerPlants()) {
                    for (StorageEntity storage : dpp.getStorages()) {
                        if (storage.getStorageId().getId().equals(storageBusinessKey.getId())) {
                            hasStorage = true;
                        }
                    }
                }
                if (hasStorage) {
                    return !vpp.getPublished().isPublished();
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

    @Override
    public boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, ConsumerIdVO consumerBusinessKey) throws PublishException {
        try {
            Optional<VirtualPowerPlantAggregate> vppOptional = virtualPowerPlantRepository.getById(vppBusinessKey);
            if (vppOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = vppOptional.get();
                boolean hasConsumer = false;
                for (HouseholdAggregate household : vpp.getHouseholds()) {
                    for (ConsumerEntity storage : household.getConsumers()) {
                        if (storage.getConsumerId().getId().equals(consumerBusinessKey.getId())) {
                            hasConsumer = true;
                        }
                    }
                }
                if (hasConsumer) {
                    return !vpp.getPublished().isPublished();
                } else {
                    throw new PublishException("checking publish status failed. consumer does not belong to vpp");
                }
            } else {
                throw new PublishException("checking publish status failed. vpp does not exist");
            }
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new PublishException("checking publish status failed. fetching vpp failed");
        }
    }
}
