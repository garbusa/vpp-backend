package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.*;
import de.uol.vpp.masterdata.domain.services.IStorageService;
import de.uol.vpp.masterdata.domain.services.StorageServiceException;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = StorageServiceException.class)
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements IStorageService {

    private final IStorageRepository repository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<StorageEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws StorageServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(dppBusinessKey));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new StorageServiceException(
                    String.format("There is no dpp %s to find any storages", dppBusinessKey)
            );
        } catch (StorageRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<StorageEntity> getAllByHouseholdId(String householdBusinessKey) throws StorageServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdBusinessKey));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new StorageServiceException(
                    String.format("There is no household %s to find any storages", householdBusinessKey)
            );
        } catch (StorageRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public StorageEntity get(String businessKey) throws StorageServiceException {
        try {
            return repository.getById(new StorageIdVO(businessKey))
                    .orElseThrow(() -> new StorageServiceException(String.format("Can't find storage by id %s", businessKey)));
        } catch (StorageException | StorageRepositoryException e) {
            throw new StorageServiceException(String.format("Can't find storage by id %s", businessKey));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(StorageEntity domainEntity, String dppBusinessKey) throws StorageServiceException {
        try {
            if (repository.getById(domainEntity.getStorageId()).isPresent()) {
                throw new StorageServiceException(
                        String.format("storage with id %s already exists", domainEntity.getStorageId().getId()));
            }
            Optional<DecentralizedPowerPlantAggregate> dppOptional = decentralizedPowerPlantRepository.getById(
                    new DecentralizedPowerPlantIdVO(dppBusinessKey)
            );
            if (dppOptional.isPresent()) {
                DecentralizedPowerPlantAggregate dpp = dppOptional.get();
                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new StorageServiceException(
                        String.format("Failed to assign Storage %s to dpp %s", domainEntity.getStorageId().getId(),
                                dppBusinessKey)
                );
            }
        } catch (StorageRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(StorageEntity domainEntity, String householdBusinessKey) throws StorageServiceException {
        try {
            if (repository.getById(domainEntity.getStorageId()).isPresent()) {
                throw new StorageServiceException(
                        String.format("storage with id %s already exists", domainEntity.getStorageId().getId()));
            }
            Optional<HouseholdAggregate> householdOptional = householdRepository.getById(
                    new HouseholdIdVO(householdBusinessKey)
            );
            if (householdOptional.isPresent()) {
                HouseholdAggregate household = householdOptional.get();
                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new StorageServiceException(
                        String.format("Failed to assign Storage %s to household %s", domainEntity.getStorageId().getId(),
                                householdBusinessKey)
                );
            }
        } catch (StorageRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey, String vppBusinessKey) throws StorageServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new StorageIdVO(businessKey))) {
                repository.deleteById(new StorageIdVO(businessKey));
            } else {
                throw new StorageServiceException("failed to delete storage. vpp has to be unpublished");
            }

        } catch (StorageRepositoryException | StorageException | VirtualPowerPlantException | PublishException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(String businessKey, Double capacity, String vppBusinessKey) throws StorageServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new StorageIdVO(businessKey))) {
                repository.updateStatus(new StorageIdVO(businessKey), new StorageStatusVO(capacity));
            } else {
                throw new StorageServiceException("failed to update storage status. vpp has to be unpublished");
            }

        } catch (StorageRepositoryException | StorageException | VirtualPowerPlantException | PublishException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String businessKey, StorageEntity domainEntity, String vppBusinessKey) throws StorageServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new StorageIdVO(businessKey))) {
                repository.update(new StorageIdVO(businessKey), domainEntity);
            }
        } catch (PublishException | VirtualPowerPlantException | StorageException | StorageRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

}