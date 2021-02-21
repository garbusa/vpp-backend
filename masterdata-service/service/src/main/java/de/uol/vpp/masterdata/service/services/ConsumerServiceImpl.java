package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.repositories.ConsumerrRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IConsumerRepository;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.services.ConsumerServiceException;
import de.uol.vpp.masterdata.domain.services.IConsumerService;
import de.uol.vpp.masterdata.domain.valueobjects.ConsumerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements IConsumerService {

    private final IConsumerRepository repository;
    private final IHouseholdRepository householdRepository;

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
        } catch (ConsumerrRepositoryException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public ConsumerEntity get(String businessKey) throws ConsumerServiceException {
        try {
            return repository.getById(new ConsumerIdVO(businessKey))
                    .orElseThrow(() -> new ConsumerServiceException(String.format("Can't find producer by id %s", businessKey)));
        } catch (ConsumerrRepositoryException e) {
            throw new ConsumerServiceException(String.format("Can't find consumer by id %s", businessKey));
        }

    }

    @Override
    public void save(ConsumerEntity domainEntity, String householdBusinessKey) throws ConsumerServiceException {
        try {
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
        } catch (ConsumerrRepositoryException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey) throws ConsumerServiceException {
        try {
            repository.deleteById(new ConsumerIdVO(businessKey));
        } catch (ConsumerrRepositoryException e) {
            throw new ConsumerServiceException(e.getMessage(), e);
        }
    }
}
