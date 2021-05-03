package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
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
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.StorageIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
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
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<StorageEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws StorageServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new StorageServiceException(
                    String.format("DK %s konnte nicht gefunden werden um Speicher abzufragen", decentralizedPowerPlantId)
            );
        } catch (StorageRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<StorageEntity> getAllByHouseholdId(String householdId) throws StorageServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdId));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new StorageServiceException(
                    String.format("Haushalt %s konnte nicht gefunden werden um Speicher abzufragen", householdId)
            );
        } catch (StorageRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public StorageEntity get(String storageId) throws StorageServiceException {
        try {
            return repository.getById(new StorageIdVO(storageId))
                    .orElseThrow(() -> new StorageServiceException(String.format(
                            "Speicher %s konnte nicht gefunden werden", storageId)));
        } catch (StorageException | StorageRepositoryException e) {
            throw new StorageServiceException(String.format("Speicher %s konnte nicht gefunden werden", storageId));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(StorageEntity domainEntity, String decentralizedPowerPlantId) throws StorageServiceException {
        try {
            if (repository.getById(domainEntity.getStorageId()).isPresent()) {
                throw new StorageServiceException(
                        String.format("Speicher %s existiert bereits", domainEntity.getStorageId().getValue()));
            }
            Optional<DecentralizedPowerPlantAggregate> dppOptional = decentralizedPowerPlantRepository.getById(
                    new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId)
            );
            if (dppOptional.isPresent()) {
                DecentralizedPowerPlantAggregate dpp = dppOptional.get();
                VirtualPowerPlantAggregate vpp = virtualPowerPlantRepository.getByDpp(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
                if (!publishUtil.isEditable(vpp.getVirtualPowerPlantId(),
                        dpp.getDecentralizedPowerPlantId())) {
                    throw new StorageServiceException(
                            String.format("Speicher %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getStorageId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new StorageServiceException(
                        String.format("Zuweisung des Speichers %s zum DK %s ist fehlgeschlagen. DK konnte nicht gefunden werden", domainEntity.getStorageId().getValue(),
                                decentralizedPowerPlantId)
                );
            }
        } catch (StorageRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(StorageEntity domainEntity, String householdId) throws StorageServiceException {
        try {
            if (repository.getById(domainEntity.getStorageId()).isPresent()) {
                throw new StorageServiceException(
                        String.format("Speicher %s existiert bereits", domainEntity.getStorageId().getValue()));
            }
            Optional<HouseholdAggregate> householdOptional = householdRepository.getById(
                    new HouseholdIdVO(householdId)
            );
            if (householdOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = virtualPowerPlantRepository.getByHousehold(new HouseholdIdVO(householdId));
                HouseholdAggregate household = householdOptional.get();
                if (!publishUtil.isEditable(vpp.getVirtualPowerPlantId(),
                        household.getHouseholdId())) {
                    throw new StorageServiceException(
                            String.format("Speicher %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getStorageId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new StorageServiceException(
                        String.format("Zuweisung des Speichers %s zum Haushalt %s ist fehlgeschlagen. Haushalt konnte nicht gefunden werden", domainEntity.getStorageId().getValue(),
                                householdId)
                );
            }
        } catch (StorageRepositoryException | HouseholdException | HouseholdRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String storageId, String virtualPowerPlantId) throws StorageServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new StorageIdVO(storageId))) {
                repository.deleteById(new StorageIdVO(storageId));
            } else {
                throw new StorageServiceException("Speicher konnte nicht gelöscht werden, da VK veröffentlicht ist");
            }

        } catch (StorageRepositoryException | StorageException | VirtualPowerPlantException | PublishException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String storageId, StorageEntity domainEntity, String virtualPowerPlantId) throws StorageServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new StorageIdVO(storageId))) {
                repository.update(new StorageIdVO(storageId), domainEntity);
            } else {
                throw new StorageServiceException("Speicher konnte nicht bearbeitet werden, da VK veröffentlicht ist");
            }
        } catch (PublishException | VirtualPowerPlantException | StorageException | StorageRepositoryException e) {
            throw new StorageServiceException(e.getMessage(), e);
        }
    }

}
