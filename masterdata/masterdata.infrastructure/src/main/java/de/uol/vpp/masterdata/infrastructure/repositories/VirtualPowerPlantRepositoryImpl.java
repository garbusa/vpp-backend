package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IVirtualPowerPlantRepository}
 */
@RequiredArgsConstructor
@Service
public class VirtualPowerPlantRepositoryImpl implements IVirtualPowerPlantRepository {

    private final VirtualPowerPlantJpaRepository jpaRepository;

    private final WindEnergyJpaRepository windJpaRepository;
    private final WaterEnergyJpaRepository waterJpaRepository;
    private final SolarEnergyJpaRepository solarJpaRepository;
    private final OtherEnergyJpaRepository otherJpaRepository;
    private final StorageJpaRepository storageJpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;

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
        Optional<VirtualPowerPlant> result = jpaRepository.findOneById(id.getValue());
        if (result.isPresent()) {
            VirtualPowerPlant vpp = result.get();

            //Delete all children
            vpp.getHouseholds().forEach((h) -> {
                h.getWaters().forEach(waterJpaRepository::delete);
                h.getWinds().forEach(windJpaRepository::delete);
                h.getSolars().forEach(solarJpaRepository::delete);
                h.getOthers().forEach(otherJpaRepository::delete);
                h.getStorages().forEach(storageJpaRepository::delete);
                householdJpaRepository.delete(h);
            });

            vpp.getDecentralizedPowerPlants().forEach((dpp) -> {
                dpp.getWaters().forEach(waterJpaRepository::delete);
                dpp.getWinds().forEach(windJpaRepository::delete);
                dpp.getSolars().forEach(solarJpaRepository::delete);
                dpp.getOthers().forEach(otherJpaRepository::delete);
                dpp.getStorages().forEach(storageJpaRepository::delete);
                decentralizedPowerPlantJpaRepository.delete(dpp);
            });

            jpaRepository.delete(result.get());
        } else {
            throw new VirtualPowerPlantRepositoryException(
                    String.format("Das VK %s kann nicht gelöscht werden, da das VK nicht gefunden wurde.", id.getValue())
            );
        }
    }

    @Override
    public void publish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlant> result = jpaRepository.findOneById(id.getValue());
        if (result.isPresent()) {
            VirtualPowerPlant vpp = result.get();
            vpp.setPublished(true);
            jpaRepository.save(vpp);
        } else {
            throw new VirtualPowerPlantRepositoryException(
                    String.format("Das VK %s kann nicht veröffentlicht werden, da das VK nicht gefunden wurde.", id.getValue())
            );
        }
    }

    @Override
    public void unpublish(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlant> result = jpaRepository.findOneById(id.getValue());
        if (result.isPresent()) {
            VirtualPowerPlant vpp = result.get();
            vpp.setPublished(false);
            jpaRepository.save(vpp);
        } else {
            throw new VirtualPowerPlantRepositoryException(
                    String.format("Die Veröffentlichung des VK %s kann nicht rückgängig gemacht werden, da das VK nicht gefunden wurde.", id.getValue())
            );
        }
    }

    @Override
    public boolean isPublished(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlantAggregate> vpp = this.getById(id);
        if (vpp.isPresent()) {
            return vpp.get().getPublished().isValue();
        } else {
            throw new VirtualPowerPlantRepositoryException(String.format("Das VK %s konnte nicht veröffentlicht werden, da VK nicht gefunden wurde.", id.getValue()));
        }
    }

    @Override
    public Optional<VirtualPowerPlantAggregate> getById(VirtualPowerPlantIdVO id) throws VirtualPowerPlantRepositoryException {
        try {
            Optional<VirtualPowerPlant> result = jpaRepository.findOneById(id.getValue());
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
    public VirtualPowerPlantAggregate getByDpp(DecentralizedPowerPlantIdVO decentralizedPowerPlantId) throws VirtualPowerPlantRepositoryException {
        try {
            List<VirtualPowerPlant> vpps = jpaRepository.findAll();

            for (VirtualPowerPlant vpp : vpps) {
                for (DecentralizedPowerPlant dpp : vpp.getDecentralizedPowerPlants()) {
                    if (dpp.getId().equals(decentralizedPowerPlantId.getValue())) {
                        return converter.toDomain(vpp);
                    }
                }
            }
        } catch (VirtualPowerPlantException e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }
        throw new VirtualPowerPlantRepositoryException(
                String.format("Beim Aufrufen eines VK mittels dem DK %s ist ein Fehler aufgetreten.", decentralizedPowerPlantId.getValue())
        );
    }

    @Override
    public VirtualPowerPlantAggregate getByHousehold(HouseholdIdVO householdId) throws VirtualPowerPlantRepositoryException {
        try {
            List<VirtualPowerPlant> vpps = jpaRepository.findAll();

            for (VirtualPowerPlant vpp : vpps) {
                for (Household dpp : vpp.getHouseholds()) {
                    if (dpp.getId().equals(householdId.getValue())) {
                        return converter.toDomain(vpp);
                    }
                }
            }
        } catch (VirtualPowerPlantException e) {
            throw new VirtualPowerPlantRepositoryException(e.getMessage(), e);
        }
        throw new VirtualPowerPlantRepositoryException(
                String.format("Beim Aufrufen eines VK mittels dem Haushalt %s ist ein Fehler aufgetreten.", householdId.getValue())
        );
    }

    @Override
    public void update(VirtualPowerPlantIdVO id, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantRepositoryException {
        Optional<VirtualPowerPlant> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            VirtualPowerPlant jpaEntity = jpaEntityOptional.get();
            VirtualPowerPlant updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setId(updated.getId());
            jpaRepository.save(jpaEntity);
        } else {
            throw new VirtualPowerPlantRepositoryException(
                    String.format("Das VK %s konnte nicht aktualisiert werden, da das VK nicht gefunden wurde.", id.getValue())
            );
        }
    }

}
