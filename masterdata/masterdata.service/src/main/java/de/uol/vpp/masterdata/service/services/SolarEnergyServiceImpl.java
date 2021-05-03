package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.entities.SolarEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.*;
import de.uol.vpp.masterdata.domain.services.ISolarEnergyService;
import de.uol.vpp.masterdata.domain.services.ProducerServiceException;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.SolarEnergyIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = ProducerServiceException.class)
@Service
@RequiredArgsConstructor
public class SolarEnergyServiceImpl implements ISolarEnergyService {

    private final ISolarEnergyRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<SolarEnergyEntity> getAllByDecentralizedPowerPlantId(String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new ProducerServiceException(
                    String.format("DK %s konnte nicht gefunden werden um Solaranlagen abzufragen", decentralizedPowerPlantId)
            );
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<SolarEnergyEntity> getAllByHouseholdId(String householdId) throws ProducerServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdId));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new ProducerServiceException(
                    String.format("Haushalt %s konnte nicht gefunden werden um Solaranlagen abzufragen", householdId)
            );
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public SolarEnergyEntity get(String solarEnergyId) throws ProducerServiceException {
        try {
            return repository.getById(new SolarEnergyIdVO(solarEnergyId))
                    .orElseThrow(() -> new ProducerServiceException(String.format("Solaranlage %s konnte nicht gefunden werden", solarEnergyId)));
        } catch (ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(String.format("Solaranlage %s konnte nicht gefunden werden", solarEnergyId));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(SolarEnergyEntity domainEntity, String decentralizedPowerPlantId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Solaranlage %s existiert bereits", domainEntity.getId().getValue()));
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
                            String.format("Solaranlage %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }
                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new ProducerServiceException(
                        String.format("Solaranlage %s konnte nicht zugewiesen werden, da DK %s nicht gefunden wurde", domainEntity.getId().getValue(),
                                decentralizedPowerPlantId)
                );
            }
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(SolarEnergyEntity domainEntity, String householdId) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("Solaranlage %s existiert bereits", domainEntity.getId().getValue()));
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
                            String.format("Solaranlage %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getId().getValue(),
                                    vpp.getVirtualPowerPlantId().getValue())
                    );
                }

                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new ProducerServiceException(
                        String.format("Solaranlage %s konnte nicht zugewiesen werden, da Haushalt %s nicht gefunden wurde", domainEntity.getId().getValue(),
                                householdId)
                );
            }
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException | VirtualPowerPlantRepositoryException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String solarEnergyId, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new SolarEnergyIdVO(solarEnergyId))) {
                repository.deleteById(new SolarEnergyIdVO(solarEnergyId));
            } else {
                throw new ProducerServiceException(
                        String.format("Solaranlage %s konnte nicht gelöscht werden, VK %s veröffentlicht ist", solarEnergyId, virtualPowerPlantId)
                );
            }
        } catch (ProducerRepositoryException | ProducerException | VirtualPowerPlantException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String solarEnergyId, SolarEnergyEntity domainEntity, String virtualPowerPlantId) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new SolarEnergyIdVO(solarEnergyId))) {
                repository.update(new SolarEnergyIdVO(solarEnergyId), domainEntity);
            } else {
                throw new ProducerServiceException(
                        String.format("Solaranlage %s konnte nicht bearbeitet werden, VK %s veröffentlicht ist", solarEnergyId, virtualPowerPlantId)
                );
            }
        } catch (PublishException | VirtualPowerPlantException | ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

}
