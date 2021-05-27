package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IWaterEnergyRepository;
import de.uol.vpp.masterdata.domain.services.IWaterEnergyService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.WaterEnergyIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IWaterEnergyService}
 */
@Transactional(rollbackFor = ProducerServiceException.class)
@Service
@RequiredArgsConstructor
public class WaterEnergyServiceImpl implements IWaterEnergyService {

    private final IWaterEnergyRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<WaterEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new ProducerServiceException(
                    String.format("DK %s konnte nicht gefunden werden um Wasserkraftanlagen abzufragen", decentralizedPowerPlantId)
            );
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<WaterEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdId));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new ProducerServiceException(
                    String.format("Haushalt %s konnte nicht gefunden werden um Wasserkraftanlagen abzufragen", householdId)
            );
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public WaterEnergyEntity get(String waterEnergyId) throws ProducerServiceException {
        try {
            return repository.getById(new WaterEnergyIdVO(waterEnergyId))
                    .orElseThrow(() -> new ProducerServiceException(
                            String.format("Wasserkraftanlage %s konnte nicht gefunden werden", waterEnergyId)));
        } catch (ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(String.format("Wasserkraftanlage %s konnte nicht gefunden werden", waterEnergyId));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(WaterEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Wasserkraftanlage %s existiert bereits", domainEntity.getId().getValue()));
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
                            String.format("Wasserkraftanlage %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new ProducerServiceException(
                        String.format("Wasserkraftanlage %s konnte dem DK %s nicht zugewiesen werden, da DK nicht gefunden wurde", domainEntity.getId().getValue(),
                                decentralizedPowerPlantId)
                );
            }
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(WaterEnergyEntity domainEntity, String householdId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Wasserkraftanlage %s existiert bereits", domainEntity.getId().getValue()));
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
                            String.format("Wasserkraftanlage %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new ProducerServiceException(
                        String.format("Wasserkraftanlage %s konnte dem Haushalt %s nicht zugewiesen werden, da Haushalt nicht gefunden wurde", domainEntity.getId().getValue(),
                                householdId)
                );
            }
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException | PublishException | VirtualPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String waterEnergyId, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new WaterEnergyIdVO(waterEnergyId))) {
                repository.deleteById(new WaterEnergyIdVO(waterEnergyId));
            } else {
                throw new ProducerServiceException("Wasserkraftanlage %s konnte nicht gelöscht werden, da VK veröffentlicht ist");
            }
        } catch (ProducerRepositoryException | ProducerException | VirtualPowerPlantException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String waterEnergyId, WaterEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new WaterEnergyIdVO(waterEnergyId))) {
                repository.update(new WaterEnergyIdVO(waterEnergyId), domainEntity);
            } else {
                throw new ProducerServiceException("Wasserkraftanlage %s konnte nicht bearbeitet werden, da VK veröffentlicht ist");
            }
        } catch (PublishException | VirtualPowerPlantException | ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

}
