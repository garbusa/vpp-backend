package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.VirtualPowerPlantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VirtualPowerPlantRepositoryImpl implements IVirtualPowerPlantRepository {

    private final VirtualPowerPlantJpaRepository jpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantRepositoryException {
        try {
            List<VirtualPowerPlantAggregate> result = new ArrayList<>();
            for (VirtualPowerPlant vpp : jpaRepository.findAll()) {
                result.add(converter.toDomain(vpp));
            }
            return result;
        } catch (VirtualPowerPlantException e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantRepositoryException {
            VirtualPowerPlant jpaEntity = converter.toInfrastructure(domainEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void deleteById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
            Optional<VirtualPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                jpaRepository.delete(result.get());
            } else {
                throw new VirtualPowerPlantRepositoryException(
                        String.format("vpp %s can not be found and can not be deleted", id.getId())
                );
            }
    }

    @Override
    public void publish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
            Optional<VirtualPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                    VirtualPowerPlant vpp = result.get();
                    vpp.setPublished(true);
                jpaRepository.save(vpp);
            } else {
                throw new VirtualPowerPlantRepositoryException(
                        String.format("vpp %s can not be found and can not be deleted", id.getId())
                );
            }
    }

    @Override
    public void unpublish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
            Optional<VirtualPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
            if (result.isPresent()) {
                    VirtualPowerPlant vpp = result.get();
                    vpp.setPublished(false);
                jpaRepository.save(vpp);
            } else {
                throw new VirtualPowerPlantRepositoryException(
                        String.format("vpp %s can not be found and can not be deleted", id.getId())
                );
            }
    }

    @Override
    public boolean isPublished(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlantAggregate> vpp = this.getById(id);
        if (vpp.isPresent()) {
            return vpp.get().getPublished().isPublished();
        } else {
            throw new VirtualPowerPlantRepositoryException(String.format("failed to find vpp with id %s", id.getId()));
        }
    }

    @Override
    public Optional<VirtualPowerPlantAggregate> getById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        try {
            Optional<VirtualPowerPlant> result = jpaRepository.findOneByBusinessKey(id.getId());
            jpaRepository.flush();
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            } else {
                return Optional.empty();
            }

        } catch (VirtualPowerPlantException e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public void update(VirtualPowerPlantIdVO id, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantRepositoryException {
            Optional<VirtualPowerPlant> jpaEntityOptional = jpaRepository.findOneByBusinessKey(id.getId());
            if (jpaEntityOptional.isPresent()) {
                VirtualPowerPlant jpaEntity = jpaEntityOptional.get();
                VirtualPowerPlant updated = converter.toInfrastructure(domainEntity);
                jpaEntity.setBusinessKey(updated.getBusinessKey());
                jpaRepository.save(jpaEntity);
            } else {
                throw new VirtualPowerPlantRepositoryException("failed to update vpp. can not find vpp entity.");
            }
    }

}
