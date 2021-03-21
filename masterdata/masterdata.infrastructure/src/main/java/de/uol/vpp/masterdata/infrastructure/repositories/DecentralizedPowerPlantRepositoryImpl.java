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
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.ProducerJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.StorageJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.VirtualPowerPlantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DecentralizedPowerPlantRepositoryImpl implements IDecentralizedPowerPlantRepository {

    private final DecentralizedPowerPlantJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final ProducerJpaRepository producerJpaRepository;
    private final StorageJpaRepository storageJpaRepository;

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
        } catch (DecentralizedPowerPlantException e) {
            throw new DecentralizedPowerPlantRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException {
        DecentralizedPowerPlant jpaEntity = converter.toInfrastructure(domainEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assign(DecentralizedPowerPlantAggregate domainEntity, VirtualPowerPlantAggregate virtualPowerPlant) throws DecentralizedPowerPlantRepositoryException {
        Optional<DecentralizedPowerPlant> jpaEntityOptional = jpaRepository.findOneByBusinessKey(domainEntity.getDecentralizedPowerPlantId().getId());
        Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlant.getVirtualPowerPlantId().getId());
        if (jpaEntityOptional.isPresent() && virtualPowerPlantOptional.isPresent()) {
            DecentralizedPowerPlant jpaEntity = jpaEntityOptional.get();
            VirtualPowerPlant virtualPowerPlantJpaEntity = virtualPowerPlantOptional.get();
            if (jpaEntity.getVirtualPowerPlant() == null) {
                jpaEntity.setVirtualPowerPlant(virtualPowerPlantJpaEntity);
                jpaRepository.save(jpaEntity);
                virtualPowerPlantJpaEntity.getDecentralizedPowerPlants().add(jpaEntity);
                virtualPowerPlantJpaRepository.save(virtualPowerPlantJpaEntity);
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
    }

    @Override
    public void update(DecentralizedPowerPlantIdVO id, DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException {
        Optional<DecentralizedPowerPlant> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntityOptional.isPresent()) {
            DecentralizedPowerPlant jpaEntity = jpaEntityOptional.get();
            jpaEntity.setBusinessKey(domainEntity.getDecentralizedPowerPlantId().getId());
            jpaRepository.save(jpaEntity);
        } else {
            throw new DecentralizedPowerPlantRepositoryException("failed to update dpp. can not find dpp entity");
        }
    }

    @Override
    public void deleteById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException {
        Optional<DecentralizedPowerPlant> jpaEntity = jpaRepository.findOneByBusinessKey(id.getId());
        if (jpaEntity.isPresent()) {
            DecentralizedPowerPlant dpp = jpaEntity.get();

            dpp.getProducers().forEach(producerJpaRepository::delete);
            dpp.getStorages().forEach(storageJpaRepository::delete);

            jpaRepository.delete(dpp);
        } else {
            throw new DecentralizedPowerPlantRepositoryException(
                    String.format("dpp %s can not be found and can not be deleted", id.getId())
            );
        }
    }


}
