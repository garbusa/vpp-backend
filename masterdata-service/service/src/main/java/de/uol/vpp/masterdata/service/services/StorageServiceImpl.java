package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IStorageRepository;
import de.uol.vpp.masterdata.domain.repositories.StorageRepositoryException;
import de.uol.vpp.masterdata.domain.services.IStorageService;
import de.uol.vpp.masterdata.domain.services.StorageServiceException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements IStorageService {

    private final IStorageRepository repository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;

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
        } catch (StorageRepositoryException e) {
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
        } catch (StorageRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public StorageEntity get(String businessKey) throws StorageServiceException {
        try {
            return repository.getById(new StorageIdVO(businessKey))
                    .orElseThrow(() -> new StorageServiceException(String.format("Can't find storage by id %s", businessKey)));
        } catch (StorageRepositoryException e) {
            throw new StorageServiceException(String.format("Can't find storage by id %s", businessKey));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(StorageEntity domainEntity, String dppBusinessKey) throws StorageServiceException {
        try {
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
        } catch (StorageRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(StorageEntity domainEntity, String householdBusinessKey) throws StorageServiceException {
        try {
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
        } catch (StorageRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey) throws StorageServiceException {
        try {
            repository.deleteById(new StorageIdVO(businessKey));
        } catch (StorageRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }
}
