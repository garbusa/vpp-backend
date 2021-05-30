package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.*;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IOtherEnergyRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.services.IOtherEnergyService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IOtherEnergyService}
 */
@Transactional(rollbackFor = ProducerServiceException.class)
@Service
@RequiredArgsConstructor
public class OtherEnergyServiceImpl implements IOtherEnergyService {

    private final IOtherEnergyRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<OtherEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new ProducerServiceException(
                    String.format("Das DK %s konnte nicht gefunden werden, um dessen alternativen Erzeugungsanlagen abzufragen.", decentralizedPowerPlantId)
            );
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<OtherEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdId));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new ProducerServiceException(
                    String.format("Der Haushalt %s konnte nicht gefunden werden, um dessen alternativen Erzeugungsanlagen abzufragenn", householdId)
            );
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public OtherEnergyEntity get(String otherEnergyId) throws ProducerServiceException {
        try {
            return repository.getById(new OtherEnergyIdVO(otherEnergyId))
                    .orElseThrow(() -> new ProducerServiceException(String.format("Die alternative Erzeugungsanlage %s konnte nicht gefunden werden.", otherEnergyId)));
        } catch (ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(String.format("Die alternative Erzeugungsanlage %s konnte nicht gefunden werden.", otherEnergyId));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(OtherEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Die alternative Erzeugungsanlage %s existiert bereits.", domainEntity.getId().getValue()));
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
                            String.format("Die alternative Erzeugungsanlage %s konnte nicht gespeichert werden, da das VK %s veröffentlicht ist.", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }

                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new ProducerServiceException(
                        String.format("Die alternative Erzeugungsanlage %s konnte dem DK %s nicht zugewiesen werden, da das DK nicht gefunden wurde.", domainEntity.getId().getValue(),
                                decentralizedPowerPlantId)
                );
            }
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(OtherEnergyEntity domainEntity, String householdId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Die alternative Erzeugungsanlage %s existiert bereits.", domainEntity.getId().getValue()));
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
                            String.format("Die alternative Erzeugungsanlage %s konnte nicht gespeichert werden, da das VK %s veröffentlicht ist.", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }

                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new ProducerServiceException(
                        String.format("Die alternative Erzeugungsanlage %s konnte dem Haushalt %s nicht zugewiesen werden, da der Haushalt nicht gefunden wurde.", domainEntity.getId().getValue(),
                                householdId)
                );
            }
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String otherEnergyId, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new OtherEnergyIdVO(otherEnergyId))) {
                repository.deleteById(new OtherEnergyIdVO(otherEnergyId));
            } else {
                throw new ProducerServiceException(
                        String.format("Die alternative Erzeugungsanlage %s konnte nicht gelöscht werden, da das VK %s veröffentlich ist.", otherEnergyId, virtualPowerPlantId)
                );
            }
        } catch (ProducerRepositoryException | ProducerException | VirtualPowerPlantException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String otherEnergyId, OtherEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new OtherEnergyIdVO(otherEnergyId))) {
                repository.update(new OtherEnergyIdVO(otherEnergyId), domainEntity);
            } else {
                throw new ProducerServiceException(
                        String.format("Die alternative Erzeugungsanlage %s konnte nicht bearbeitet werden, da das VK %s veröffentlich ist", otherEnergyId, virtualPowerPlantId)
                );
            }
        } catch (PublishException | VirtualPowerPlantException | ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

}
