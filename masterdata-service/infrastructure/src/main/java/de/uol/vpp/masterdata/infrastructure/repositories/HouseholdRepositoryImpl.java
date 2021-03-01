package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.repositories.HouseholdRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.VirtualPowerPlantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HouseholdRepositoryImpl implements IHouseholdRepository {

    private final HouseholdJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<HouseholdAggregate> getAllByVirtualPowerPlant(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws HouseholdRepositoryException {
        try {
            Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlantAggregate.getVirtualPowerPlantId().getId());
            if (virtualPowerPlantOptional.isPresent()) {
                List<HouseholdAggregate> result = new ArrayList<>();
                for (Household household : jpaRepository.findAllByVirtualPowerPlant(virtualPowerPlantOptional.get())) {
                    result.add(converter.toDomain(household));
                }
                return result;
            } else {
                throw new HouseholdRepositoryException(
                        String.format("There is no VPP with id %s to get all households", virtualPowerPlantAggregate.getVirtualPowerPlantId().getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new HouseholdRepositoryException("failed to get all households. constraint violation occured.");
        } catch (HouseholdException e) {
            throw new HouseholdRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<HouseholdAggregate> getById(HouseholdIdVO id) throws HouseholdRepositoryException {
        try {
            Optional<Household> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (DataIntegrityViolationException e) {
            throw new HouseholdRepositoryException("failed to get household. constraint violation occured.");
        } catch (HouseholdException e) {
            throw new HouseholdRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(HouseholdAggregate entity) throws HouseholdRepositoryException {
        try {
            Household jpaEntity = converter.toInfrastructure(entity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (DataIntegrityViolationException e) {
            throw new HouseholdRepositoryException("failed to save household. constraint violation occured.");
        }
    }

    @Override
    public void deleteById(HouseholdIdVO id) throws HouseholdRepositoryException {
        try {
            Optional<Household> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntity.isPresent()) {
                try {
                    jpaRepository.delete(jpaEntity.get());
                } catch (Exception e) {
                    throw new HouseholdRepositoryException(e.getMessage(), e);
                }
            } else {
                throw new HouseholdRepositoryException(
                        String.format("household %s can not be found and can not be deleted", id.getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new HouseholdRepositoryException("failed to delete household. constraint violation occured.");
        }
    }

    @Override
    public void assign(HouseholdAggregate entity, VirtualPowerPlantAggregate virtualPowerPlant) throws HouseholdRepositoryException {
        try {
            Optional<Household> jpaEntityOptional = jpaRepository.findOneByBusinessKey(entity.getHouseholdId().getId());
            Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlant.getVirtualPowerPlantId().getId());
            if (jpaEntityOptional.isPresent() && virtualPowerPlantOptional.isPresent()) {
                Household jpaEntity = jpaEntityOptional.get();
                VirtualPowerPlant virtualPowerPlantJpaEntity = virtualPowerPlantOptional.get();
                if (jpaEntity.getVirtualPowerPlant() == null) {
                    jpaEntity.setVirtualPowerPlant(virtualPowerPlantJpaEntity);
                    jpaRepository.saveAndFlush(jpaEntity);
                    virtualPowerPlantJpaEntity.getHouseholds().add(jpaEntity);
                    virtualPowerPlantJpaRepository.saveAndFlush(virtualPowerPlantJpaEntity);
                } else {
                    throw new HouseholdRepositoryException(
                            String.format("Dpp %s is already assigned to vpp %s", entity.getHouseholdId().getId(),
                                    jpaEntity.getVirtualPowerPlant().getBusinessKey())
                    );
                }
            } else {
                throw new HouseholdRepositoryException(
                        String.format("An error occured while assigning dpp %s to vpp %s", entity.getHouseholdId().getId(),
                                virtualPowerPlant.getVirtualPowerPlantId().getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new HouseholdRepositoryException("failed to assign household. constraint violation occured.");
        }
    }

    @Override
    public void update(HouseholdIdVO id, HouseholdAggregate domainEntity) throws HouseholdRepositoryException {
        try {
            Optional<Household> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                Household jpaEntity = jpaEntityOptional.get();
                Household updated = converter.toInfrastructure(domainEntity);
                jpaEntity.setBusinessKey(updated.getBusinessKey());
                jpaEntity.setMemberAmount(updated.getMemberAmount());
                jpaRepository.saveAndFlush(jpaEntity);
            } else {
                throw new HouseholdRepositoryException("failed to update household. can not find household entity.");
            }
        } catch (DataIntegrityViolationException e) {
            throw new HouseholdRepositoryException("failed to update household. constraint violation occured.");
        }
    }

}
