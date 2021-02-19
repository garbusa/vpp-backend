package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.VirtualPowerPlantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VirtualPowerPlantRepositoryImpl implements IVirtualPowerPlantRepository {

    private final VirtualPowerPlantJpaRepository jpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantRepositoryException {
        try {
            return jpaRepository.findAll().stream().map(converter::toDomain).collect(Collectors.toList());
        } catch (Exception e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<VirtualPowerPlantAggregate> getById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        try {
            Optional<VirtualPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
            return result.map(converter::toDomain);
        } catch (Exception e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantRepositoryException {
        try {
            VirtualPowerPlant jpaEntity = converter.toInfrastructure(domainEntity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (Exception e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public void deleteById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
        if (result.isPresent()) {
            try {
                jpaRepository.delete(result.get());
            } catch (Exception e) {
                throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
            }
        } else {
            throw new VirtualPowerPlantRepositoryException(
                    String.format("vpp %s can not be found and can not be deleted", id.getId())
            );
        }
    }
}
