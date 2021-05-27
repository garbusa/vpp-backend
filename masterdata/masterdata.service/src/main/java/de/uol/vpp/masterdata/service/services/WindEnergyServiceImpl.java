package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.WindEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IWindEnergyRepository;
import de.uol.vpp.masterdata.domain.services.IWindEnergyService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.WindEnergyIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IWindEnergyService}
 */
@Transactional(rollbackFor = ProducerServiceException.class)
@Service
@RequiredArgsConstructor
public class WindEnergyServiceImpl implements IWindEnergyService {

    private final IWindEnergyRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<WindEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new ProducerServiceException(
                    String.format("DK %s konnte nicht gefunden werden um Windkraftanlage abzufragen", decentralizedPowerPlantId)
            );
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<WindEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdId));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new ProducerServiceException(
                    String.format("Haushalt %s konnte nicht gefunden werden um Windkraftanlage abzufragen", householdId)
            );
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public WindEnergyEntity get(String windEnergyId) throws ProducerServiceException {
        try {
            return repository.getById(new WindEnergyIdVO(windEnergyId))
                    .orElseThrow(() -> new ProducerServiceException(String.format("Windkraftanlage %s konnte nicht gefunden werden", windEnergyId)));
        } catch (ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(String.format("Windkraftanlage %s konnte nicht gefunden werden", windEnergyId));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(WindEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Windkraftanlage %s existiert bereits", domainEntity.getId().getValue()));
            }
            Optional<DecentralizedPowerPlantAggregate> dppOptional = decentralizedPowerPlantRepository.getById(
                    new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId)
            );

            if (dppOptional.isPresent()) {
                DecentralizedPowerPlantAggregate dpp = dppOptional.get();
                VirtualPowerPlantAggregate vpp = virtualPowerPlantRepository.getByDpp(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
                if (!publishUtil.isEditable(vpp.getVirtualPowerPlantId(),
                        dpp.getDecentralizedPowerPlantId())) {
                    throw new ProducerServiceException(
                            String.format("Windkraftanlage %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new ProducerServiceException(
                        String.format("Windkraftanlage %s konnte dem DK %s nicht zugewiesen werden, da DK nicht gefunden wurde", domainEntity.getId().getValue(),
                                decentralizedPowerPlantId)
                );
            }
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(WindEnergyEntity domainEntity, String householdId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Windkraftanlage %s konnte nicht gefunden werden", domainEntity.getId().getValue()));
            }
            Optional<HouseholdAggregate> householdOptional = householdRepository.getById(
                    new HouseholdIdVO(householdId)
            );

            if (householdOptional.isPresent()) {
                VirtualPowerPlantAggregate vpp = virtualPowerPlantRepository.getByHousehold(new HouseholdIdVO(householdId));
                HouseholdAggregate household = householdOptional.get();
                if (!publishUtil.isEditable(vpp.getVirtualPowerPlantId(),
                        household.getHouseholdId())) {
                    throw new ProducerServiceException(
                            String.format("Windkraftanlage %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new ProducerServiceException(
                        String.format("Windkraftanlage %s konnte dem Haushalt %s nicht zugewiesen werden, da Haushalt nicht gefunden wurde", domainEntity.getId().getValue(),
                                householdId)
                );
            }
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String windEnergyId, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new WindEnergyIdVO(windEnergyId))) {
                repository.deleteById(new WindEnergyIdVO(windEnergyId));
            } else {
                throw new ProducerServiceException(String.format("Windkraftanlage %s konnte nicht gelöscht werden da VK veröffentlicht ist", windEnergyId));
            }
        } catch (ProducerRepositoryException | ProducerException | VirtualPowerPlantException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String windEnergyId, WindEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new WindEnergyIdVO(windEnergyId))) {
                repository.update(new WindEnergyIdVO(windEnergyId), domainEntity);
            } else {
                throw new ProducerServiceException(String.format("Windkraftanlage %s konnte nicht bearbeitet werden da VK veröffentlicht ist", windEnergyId));
            }
        } catch (PublishException | VirtualPowerPlantException | ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

}
