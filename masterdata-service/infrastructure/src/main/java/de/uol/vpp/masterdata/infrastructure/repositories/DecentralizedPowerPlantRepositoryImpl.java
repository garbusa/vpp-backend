package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DecentralizedPowerPlantRepositoryImpl implements IDecentralizedPowerPlantRepository {

    private final DecentralizedPowerPlantJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<DecentralizedPowerPlantAggregate> getAllByVppKey(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlantAggregate.getVirtualPowerPlantId().getId());
        return virtualPowerPlantOptional.map(virtualPowerPlant -> jpaRepository.findAllByVirtualPowerPlant(virtualPowerPlant)
                .stream()
                .map(converter::toDomain)
                .collect(Collectors.toList()))
                .orElseThrow(() -> new VirtualPowerPlantRepositoryException(
                        String.format("There is no VPP with id %s", virtualPowerPlantAggregate.getVirtualPowerPlantId().getId())
                ));
    }

    @Override
    public Optional<DecentralizedPowerPlantAggregate> getById(DecentralizedPowerPlantIdVO id) {
        Optional<DecentralizedPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
        return result.map(converter::toDomain);
    }

    @Override
    public void save(DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException {
        try {
            DecentralizedPowerPlant jpaEntity = converter.toInfrastructure(domainEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (Exception e) {
            throw new DecentralizedPowerPlantRepositoryException(e.getMessage(), e);
        }
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
    }

    @Override
    public void deleteById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException {
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
    }


}
