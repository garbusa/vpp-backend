package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IHouseholdService}
 */
@Transactional(rollbackFor = HouseholdServiceException.class)
@RequiredArgsConstructor
@Service
public class HouseholdServiceImpl implements IHouseholdService {

    private final IHouseholdRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<HouseholdAggregate> getAllByVppId(String virtualPowerPlantId) throws HouseholdServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional;
            virtualPowerPlantOptional = virtualPowerPlantRepository.getById(new VirtualPowerPlantIdVO(virtualPowerPlantId));
            if (virtualPowerPlantOptional.isPresent()) {
                return repository.getAllByVirtualPowerPlant(virtualPowerPlantOptional.get());
            }
            throw new HouseholdServiceException(
                    String.format("VK %s konnte nicht gefunden werden um Haushalte abzufragen", virtualPowerPlantId)
            );
        } catch (HouseholdRepositoryException | VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }

    }

    @Override
    public HouseholdAggregate get(String householdId) throws HouseholdServiceException {
        try {
            return repository.getById(new HouseholdIdVO(householdId))
                    .orElseThrow(() -> new HouseholdServiceException(String.format("Haushalt %s konnte nicht gefunden werden", householdId)));
        } catch (HouseholdException | HouseholdRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void save(HouseholdAggregate domainEntity, String virtualPowerPlantId) throws HouseholdServiceException {
        try {
            if (repository.getById(domainEntity.getHouseholdId()).isPresent()) {
                throw new HouseholdServiceException(
                        String.format("Haushalt %s existiert bereits", domainEntity.getHouseholdId().getValue()));
            }
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional = virtualPowerPlantRepository.getById(
                    new VirtualPowerPlantIdVO(virtualPowerPlantId)
            );

            if (virtualPowerPlantOptional.isPresent()) {

                if (!publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId),
                        domainEntity.getHouseholdId())) {
                    throw new HouseholdServiceException(
                            String.format("Haushalt %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getHouseholdId().getValue(),
                                    virtualPowerPlantId)
                    );
                }

                VirtualPowerPlantAggregate virtualPowerPlant = virtualPowerPlantOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, virtualPowerPlant);
            } else {
                throw new HouseholdServiceException(
                        String.format("Haushalt %s konnte VK %s nicht zugewiesen werden, da Haushalt bereits zugewiesen wurde", domainEntity.getHouseholdId().getValue(),
                                virtualPowerPlantId)
                );
            }
        } catch (HouseholdRepositoryException | VirtualPowerPlantRepositoryException | VirtualPowerPlantException | PublishException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String householdId, String virtualPowerPlantId) throws HouseholdServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new HouseholdIdVO(householdId))) {
                repository.deleteById(new HouseholdIdVO(householdId));
            } else {
                throw new HouseholdServiceException(
                        String.format("Haushalt %s konnte nicht gelöscht werden, da VK veröffentlicht ist", householdId)
                );
            }

        } catch (HouseholdRepositoryException | HouseholdException | VirtualPowerPlantException | PublishException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String householdId, HouseholdAggregate domainEntity, String virtualPowerPlantId) throws HouseholdServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new HouseholdIdVO(householdId))) {
                repository.update(new HouseholdIdVO(householdId), domainEntity);
            } else {
                throw new HouseholdServiceException(
                        String.format("Haushalt %s konnte nicht bearbeitet werden, da VK veröffentlicht ist", householdId)
                );
            }
        } catch (PublishException | VirtualPowerPlantException | HouseholdException | HouseholdRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }
}
