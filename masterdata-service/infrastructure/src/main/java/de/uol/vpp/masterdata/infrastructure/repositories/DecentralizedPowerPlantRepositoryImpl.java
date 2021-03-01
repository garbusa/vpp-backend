package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.DecentralizedPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.VirtualPowerPlantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DecentralizedPowerPlantRepositoryImpl implements IDecentralizedPowerPlantRepository {

    private final DecentralizedPowerPlantJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<DecentralizedPowerPlantAggregate> getAllByVppKey(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlantAggregate.getVirtualPowerPlantId().getId());
            if (virtualPowerPlantOptional.isPresent()) {
                List<DecentralizedPowerPlant> vpps = jpaRepository.findAllByVirtualPowerPlant(virtualPowerPlantOptional.get());
                List<DecentralizedPowerPlantAggregate> result = new ArrayList<>();
                for (DecentralizedPowerPlant vpp : vpps) {
                    result.add(converter.toDomain(vpp));
                }
                return result;
            } else {
                throw new VirtualPowerPlantRepositoryException(
                        String.format("There is no VPP with id %s", virtualPowerPlantAggregate.getVirtualPowerPlantId().getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new DecentralizedPowerPlantRepositoryException("failed to get all dpp. constraint violation occured.");
        } catch (DecentralizedPowerPlantException | VirtualPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<DecentralizedPowerPlantAggregate> getById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            } else {
                return Optional.empty();
            }
        } catch (DataIntegrityViolationException e) {
            throw new DecentralizedPowerPlantRepositoryException("failed to delete dpp. constraint violation occured.");
        } catch (DecentralizedPowerPlantException e) {
            throw new DecentralizedPowerPlantRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException {
        try {
            DecentralizedPowerPlant jpaEntity = converter.toInfrastructure(domainEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DecentralizedPowerPlantRepositoryException("failed to save dpp. constraint violation occured.");
        }
    }

    @Override
    public void assign(DecentralizedPowerPlantAggregate domainEntity, VirtualPowerPlantAggregate virtualPowerPlant) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> jpaEntityOptional = jpaRepository.findOneByBusinessKey(domainEntity.getDecentralizedPowerPlantId().getId());
            Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlant.getVirtualPowerPlantId().getId());
            if (jpaEntityOptional.isPresent() && virtualPowerPlantOptional.isPresent()) {
                DecentralizedPowerPlant jpaEntity = jpaEntityOptional.get();
                VirtualPowerPlant virtualPowerPlantJpaEntity = virtualPowerPlantOptional.get();
                if (jpaEntity.getVirtualPowerPlant() == null) {
                    jpaEntity.setVirtualPowerPlant(virtualPowerPlantJpaEntity);
                    jpaRepository.saveAndFlush(jpaEntity);
                    virtualPowerPlantJpaEntity.getDecentralizedPowerPlants().add(jpaEntity);
                    virtualPowerPlantJpaRepository.saveAndFlush(virtualPowerPlantJpaEntity);
                } else {
                    throw new DecentralizedPowerPlantRepositoryException(
                            String.format("Dpp %s is already assigned to vpp %s", domainEntity.getDecentralizedPowerPlantId().getId(),
                                    jpaEntity.getVirtualPowerPlant().getBusinessKey())
                    );
                }
            } else {
                throw new DecentralizedPowerPlantRepositoryException(
                        String.format("An error occured while assigning dpp %s to vpp %s", domainEntity.getDecentralizedPowerPlantId().getId(),
                                virtualPowerPlant.getVirtualPowerPlantId().getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new DecentralizedPowerPlantRepositoryException("failed to assign dpp. constraint violation occured.");
        }
    }

    @Override
    public void update(DecentralizedPowerPlantIdVO id, DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                DecentralizedPowerPlant jpaEntity = jpaEntityOptional.get();
                jpaEntity.setBusinessKey(domainEntity.getDecentralizedPowerPlantId().getId());
                jpaRepository.saveAndFlush(jpaEntity);
            } else {
                throw new DecentralizedPowerPlantRepositoryException("failed to update dpp. can not find dpp entity");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DecentralizedPowerPlantRepositoryException("failed to update dpp. constraint violation occured.");
        }
    }

    @Override
    public void deleteById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntity.isPresent()) {
                try {
                    jpaRepository.delete(jpaEntity.get());
                } catch (Exception e) {
                    throw new DecentralizedPowerPlantRepositoryException(e.getMessage(), e);
                }
            } else {
                throw new DecentralizedPowerPlantRepositoryException(
                        String.format("dpp %s can not be found and can not be deleted", id.getId())
                );
            }
        } catch (DataIntegrityViolationException e) {
            throw new DecentralizedPowerPlantRepositoryException("failed to delete dpp. constraint violation occured.");
        }
    }


}
