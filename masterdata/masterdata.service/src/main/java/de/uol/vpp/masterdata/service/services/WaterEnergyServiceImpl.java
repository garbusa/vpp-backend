package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.WaterEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.*;
import de.uol.vpp.masterdata.domain.services.IWaterEnergyService;
import de.uol.vpp.masterdata.domain.services.ProducerServiceException;
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

@Transactional(rollbackFor = ProducerServiceException.class)
@Service
@RequiredArgsConstructor
public class WaterEnergyServiceImpl implements IWaterEnergyService {

    private final IWaterEnergyRepository repository;
    private final IDecentralizedPowerPlantRepository decentralizedPowerPlantRepository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<WaterEnergyEntity> getAllByDecentralizedPowerPlantId(String dppBusinessKey) throws ProducerServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = decentralizedPowerPlantRepository.getById(new DecentralizedPowerPlantIdVO(dppBusinessKey));
            if (dpp.isPresent()) {
                return repository.getAllByDecentralizedPowerPlant(dpp.get());
            }
            throw new ProducerServiceException(
                    String.format("There is no dpp %s to find any WaterEnergys", dppBusinessKey)
            );
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<WaterEnergyEntity> getAllByHouseholdId(String householdBusinessKey) throws ProducerServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdBusinessKey));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new ProducerServiceException(
                    String.format("There is no household %s to find any WaterEnergys", householdBusinessKey)
            );
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public WaterEnergyEntity get(String businessKey) throws ProducerServiceException {
        try {
            return repository.getById(new WaterEnergyIdVO(businessKey))
                    .orElseThrow(() -> new ProducerServiceException(String.format("Can't find WaterEnergy by actionRequestId %s", businessKey)));
        } catch (ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(String.format("Can't find WaterEnergy by actionRequestId %s", businessKey));
        }

    }

    @Override
    public void saveWithDecentralizedPowerPlant(WaterEnergyEntity domainEntity, String dppBusinessKey) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("WaterEnergy with actionRequestId %s already exists", domainEntity.getId().getValue()));
            }
            Optional<DecentralizedPowerPlantAggregate> dppOptional = decentralizedPowerPlantRepository.getById(
                    new DecentralizedPowerPlantIdVO(dppBusinessKey)
            );

            if (dppOptional.isPresent()) {
                DecentralizedPowerPlantAggregate dpp = dppOptional.get();
                repository.save(domainEntity);
                repository.assignToDecentralizedPowerPlant(domainEntity, dpp);
            } else {
                throw new ProducerServiceException(
                        String.format("Failed to assign WaterEnergy %s to dpp %s", domainEntity.getId().getValue(),
                                dppBusinessKey)
                );
            }
        } catch (ProducerRepositoryException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveWithHousehold(WaterEnergyEntity domainEntity, String householdBusinessKey) throws ProducerServiceException {
        try {
            if (repository.getById(domainEntity.getId()).isPresent()) {
                throw new ProducerServiceException(
                        String.format("WaterEnergy with actionRequestId %s already exists", domainEntity.getId().getValue()));
            }
            Optional<HouseholdAggregate> householdOptional = householdRepository.getById(
                    new HouseholdIdVO(householdBusinessKey)
            );

            if (householdOptional.isPresent()) {
                HouseholdAggregate household = householdOptional.get();
                repository.save(domainEntity);
                repository.assignToHousehold(domainEntity, household);
            } else {
                throw new ProducerServiceException(
                        String.format("Failed to assign WaterEnergy %s to household %s", domainEntity.getId().getValue(),
                                householdBusinessKey)
                );
            }
        } catch (ProducerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey, String vppBusinessKey) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new WaterEnergyIdVO(businessKey))) {
                repository.deleteById(new WaterEnergyIdVO(businessKey));
            } else {
                throw new ProducerServiceException("failed to delete WaterEnergy. vpp has to be unpublished");
            }
        } catch (ProducerRepositoryException | ProducerException | VirtualPowerPlantException | PublishException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String businessKey, WaterEnergyEntity domainEntity, String vppBusinessKey) throws ProducerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new WaterEnergyIdVO(businessKey))) {
                repository.update(new WaterEnergyIdVO(businessKey), domainEntity);
            }
        } catch (PublishException | VirtualPowerPlantException | ProducerException | ProducerRepositoryException e) {
            throw new ProducerServiceException(e.getMessage(), e);
        }
    }

}
