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
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HouseholdRepositoryImpl implements IHouseholdRepository {

    private final HouseholdJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final StorageJpaRepository storageJpaRepository;
    private final SolarEnergyJpaRepository solarJpaRepository;
    private final WindEnergyJpaRepository windJpaRepository;
    private final WaterEnergyJpaRepository waterJpaRepository;
    private final OtherEnergyJpaRepository otherJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<HouseholdAggregate> getAllByVirtualPowerPlant(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws HouseholdRepositoryException {
        try {
            Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneById(virtualPowerPlantAggregate.getVirtualPowerPlantId().getValue());
            if (virtualPowerPlantOptional.isPresent()) {
                List<HouseholdAggregate> result = new ArrayList<>();
                for (Household household : jpaRepository.findAllByVirtualPowerPlant(virtualPowerPlantOptional.get())) {
                    result.add(converter.toDomain(household));
                }
                return result;
            } else {
                throw new HouseholdRepositoryException(
                        String.format("VK %s konnte nicht gefunden werden um Haushälte abzufragen", virtualPowerPlantAggregate.getVirtualPowerPlantId().getValue())
                );
            }
        } catch (HouseholdException e) {
            throw new HouseholdRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<HouseholdAggregate> getById(HouseholdIdVO id) throws HouseholdRepositoryException {
        try {
            Optional<Household> result = jpaRepository.findOneById(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (HouseholdException e) {
            throw new HouseholdRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(HouseholdAggregate entity) throws HouseholdRepositoryException {
        Household jpaEntity = converter.toInfrastructure(entity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void deleteById(HouseholdIdVO id) throws HouseholdRepositoryException {
        Optional<Household> jpaEntity = jpaRepository.findOneById(id.getValue());
        if (jpaEntity.isPresent()) {
            Household household = jpaEntity.get();

            household.getSolars().forEach(solarJpaRepository::delete);
            household.getWaters().forEach(waterJpaRepository::delete);
            household.getWinds().forEach(windJpaRepository::delete);
            household.getOthers().forEach(otherJpaRepository::delete);
            household.getStorages().forEach(storageJpaRepository::delete);

            jpaRepository.delete(household);
        } else {
            throw new HouseholdRepositoryException(
                    String.format("Haushalt %s konnte nicht gelöscht werden, da Haushalt nicht gefunden wurde", id.getValue())
            );
        }
    }

    @Override
    public void assign(HouseholdAggregate entity, VirtualPowerPlantAggregate virtualPowerPlant) throws HouseholdRepositoryException {
        Optional<Household> jpaEntityOptional = jpaRepository.findOneById(entity.getHouseholdId().getValue());
        Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneById(virtualPowerPlant.getVirtualPowerPlantId().getValue());
        if (jpaEntityOptional.isPresent() && virtualPowerPlantOptional.isPresent()) {
            Household jpaEntity = jpaEntityOptional.get();
            VirtualPowerPlant virtualPowerPlantJpaEntity = virtualPowerPlantOptional.get();
            if (jpaEntity.getVirtualPowerPlant() == null) {
                jpaEntity.setVirtualPowerPlant(virtualPowerPlantJpaEntity);
                jpaRepository.save(jpaEntity);
                virtualPowerPlantJpaEntity.getHouseholds().add(jpaEntity);
                virtualPowerPlantJpaRepository.save(virtualPowerPlantJpaEntity);
            } else {
                throw new HouseholdRepositoryException(
                        String.format("Haushalt %s konnte VK %s nicht zu gewiesen werden, da Haushalt bereits zugewiesen wurde", entity.getHouseholdId().getValue(),
                                jpaEntity.getVirtualPowerPlant().getId())
                );
            }
        } else {
            throw new HouseholdRepositoryException(
                    String.format("Haushalt %s konnte VK %s nicht zugewiesen werden", entity.getHouseholdId().getValue(),
                            virtualPowerPlant.getVirtualPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void update(HouseholdIdVO id, HouseholdAggregate domainEntity) throws HouseholdRepositoryException {
        Optional<Household> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            Household jpaEntity = jpaEntityOptional.get();
            Household updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setId(updated.getId());
            jpaEntity.setMemberAmount(updated.getMemberAmount());
            jpaRepository.save(jpaEntity);
        } else {
            throw new HouseholdRepositoryException(
                    String.format("Haushalt %s konnte nicht aktualisiert werden, da Haushalt nicht gefunden wurde", id.getValue())
            );
        }
    }

}
