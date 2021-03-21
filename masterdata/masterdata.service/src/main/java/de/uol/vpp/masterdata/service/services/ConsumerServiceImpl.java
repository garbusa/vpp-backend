package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.exceptions.ConsumerException;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.ConsumerRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.HouseholdRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IConsumerRepository;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.services.ConsumerServiceException;
import de.uol.vpp.masterdata.domain.services.IConsumerService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerStatusVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = ConsumerServiceException.class)
@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements IConsumerService {

    private final IConsumerRepository repository;
    private final IHouseholdRepository householdRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<ConsumerEntity> getAllByHouseholdId(String householdBusinessKey) throws ConsumerServiceException {
        try {
            Optional<HouseholdAggregate> household = householdRepository.
                    getById(new HouseholdIdVO(householdBusinessKey));
            if (household.isPresent()) {
                return repository.getAllByHousehold(household.get());
            }
            throw new ConsumerServiceException(
                    String.format("There is no household %s to find any consumers", householdBusinessKey)
            );
        } catch (HouseholdException | ConsumerRepositoryException | HouseholdRepositoryException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public ConsumerEntity get(String businessKey) throws ConsumerServiceException {
        try {
            return repository.getById(new ConsumerIdVO(businessKey))
                    .orElseThrow(() -> new ConsumerServiceException(String.format("Can't find producer by id %s", businessKey)));
        } catch (ConsumerRepositoryException | ConsumerException e) {
            throw new ConsumerServiceException(String.format("Can't find consumer by id %s", businessKey));
        }

    }

    @Override
    public void save(ConsumerEntity domainEntity, String householdBusinessKey) throws ConsumerServiceException {
        try {
            if (repository.getById(domainEntity.getConsumerId()).isPresent()) {
                throw new ConsumerServiceException(
                        String.format("consumer with id %s already exists", domainEntity.getConsumerId().getId()));
            }
            Optional<HouseholdAggregate> householdOptional = householdRepository.getById(
                    new HouseholdIdVO(householdBusinessKey)
            );

            if (householdOptional.isPresent()) {
                HouseholdAggregate household = householdOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, household);
            } else {
                throw new ConsumerServiceException(
                        String.format("Failed to assign consumer %s to household %s", domainEntity.getConsumerId().getId(),
                                householdBusinessKey)
                );
            }
        } catch (ConsumerRepositoryException | HouseholdException | HouseholdRepositoryException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey, String vppBusinessKey) throws ConsumerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new ConsumerIdVO(businessKey))) {
                repository.deleteById(new ConsumerIdVO(businessKey));
            } else {
                throw new ConsumerServiceException("failed to delete consumer. vpp has to be unpublished");
            }
        } catch (ConsumerRepositoryException | ConsumerException | VirtualPowerPlantException | PublishException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(String businessKey, boolean isRunning, String vppBusinessKey) throws ConsumerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new ConsumerIdVO(vppBusinessKey))) {
                repository.updateStatus(new ConsumerIdVO(businessKey),
                        new ConsumerStatusVO(isRunning));
            } else {
                throw new ConsumerServiceException("failed to update consumer status. vpp has to be unpublished");
            }

        } catch (ConsumerRepositoryException | ConsumerException | VirtualPowerPlantException | PublishException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String businessKey, ConsumerEntity domainEntity, String vppBusinessKey) throws ConsumerServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new ConsumerIdVO(businessKey))) {
                repository.update(new ConsumerIdVO(businessKey), domainEntity);
            }
        } catch (PublishException | VirtualPowerPlantException | ConsumerException | ConsumerRepositoryException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }


}
