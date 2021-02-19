package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.repositories.HouseholdRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.VirtualPowerPlantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseholdRepositoryImpl implements IHouseholdRepository {

    private final HouseholdJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<HouseholdAggregate> getAllByVppKey(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws HouseholdRepositoryException {
        Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneByBusinessKey(virtualPowerPlantAggregate.getVirtualPowerPlantId().getId());
        return virtualPowerPlantOptional.map(virtualPowerPlant -> jpaRepository.findAllByVirtualPowerPlant(virtualPowerPlant)
                .stream()
                .map(converter::toDomain)
                .collect(Collectors.toList()))
                .orElseThrow(() -> new HouseholdRepositoryException(
                        String.format("There is no VPP with id %s to get all households", virtualPowerPlantAggregate.getVirtualPowerPlantId().getId())
                ));
    }

    @Override
    public Optional<HouseholdAggregate> getById(HouseholdIdVO id) {
        Optional<Household> result = jpaRepository.findOneByBusinessKey(id.getId());
        return result.map(converter::toDomain);
    }

    @Override
    public void save(HouseholdAggregate entity) throws HouseholdRepositoryException {
        try {
            Household jpaEntity = converter.toInfrastructure(entity);
            jpaRepository.saveAndFlush(jpaEntity);
        } catch (Exception e) {
            throw new HouseholdRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(HouseholdIdVO id) throws HouseholdRepositoryException {
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
    }

    @Override
    public void assign(HouseholdAggregate entity, VirtualPowerPlantAggregate virtualPowerPlant) throws HouseholdRepositoryException {
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
    }
}
