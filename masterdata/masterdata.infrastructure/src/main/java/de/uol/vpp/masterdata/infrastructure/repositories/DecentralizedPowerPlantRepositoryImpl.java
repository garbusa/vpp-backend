package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.VirtualPowerPlant;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IDecentralizedPowerPlantRepository}
 */
@RequiredArgsConstructor
@Service
public class DecentralizedPowerPlantRepositoryImpl implements IDecentralizedPowerPlantRepository {

    private final DecentralizedPowerPlantJpaRepository jpaRepository;
    private final VirtualPowerPlantJpaRepository virtualPowerPlantJpaRepository;
    private final WaterEnergyJpaRepository waterEnergyJpaRepository;
    private final WindEnergyJpaRepository windEnergyJpaRepository;
    private final SolarEnergyJpaRepository solarEnergyJpaRepository;
    private final OtherEnergyJpaRepository otherEnergyJpaRepository;
    private final StorageJpaRepository storageJpaRepository;

    private final InfrastructureEntityConverter converter;

    @Override
    public List<DecentralizedPowerPlantAggregate> getAllByVppId(VirtualPowerPlantAggregate virtualPowerPlantAggregate) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneById(virtualPowerPlantAggregate.getVirtualPowerPlantId().getValue());
            if (virtualPowerPlantOptional.isPresent()) {
                List<DecentralizedPowerPlant> vpps = jpaRepository.findAllByVirtualPowerPlant(virtualPowerPlantOptional.get());
                List<DecentralizedPowerPlantAggregate> result = new ArrayList<>();
                for (DecentralizedPowerPlant vpp : vpps) {
                    result.add(converter.toDomain(vpp));
                }
                return result;
            } else {
                throw new VirtualPowerPlantRepositoryException(
                        String.format("VK %s konnte nicht gefunden werden um DK abzufragen", virtualPowerPlantAggregate.getVirtualPowerPlantId().getValue())
                );
            }
        } catch (DecentralizedPowerPlantException | VirtualPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<DecentralizedPowerPlantAggregate> getById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> result = jpaRepository.findOneById(id.getValue());
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
        Optional<DecentralizedPowerPlant> jpaEntityOptional = jpaRepository.findOneById(domainEntity.getDecentralizedPowerPlantId().getValue());
        Optional<VirtualPowerPlant> virtualPowerPlantOptional = virtualPowerPlantJpaRepository.findOneById(virtualPowerPlant.getVirtualPowerPlantId().getValue());
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
                        String.format("DK %s konnte VK %s nicht zugewiesen werden, da DK bereits zugewiesen wurde", domainEntity.getDecentralizedPowerPlantId().getValue(),
                                jpaEntity.getVirtualPowerPlant().getId())
                );
            }
        } else {
            throw new DecentralizedPowerPlantRepositoryException(
                    String.format("DK %s konnte VK %s nicht zugewiesen werden", domainEntity.getDecentralizedPowerPlantId().getValue(),
                            virtualPowerPlant.getVirtualPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void update(DecentralizedPowerPlantIdVO id, DecentralizedPowerPlantAggregate domainEntity) throws DecentralizedPowerPlantRepositoryException {
        Optional<DecentralizedPowerPlant> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            DecentralizedPowerPlant jpaEntity = jpaEntityOptional.get();
            jpaEntity.setId(domainEntity.getDecentralizedPowerPlantId().getValue());
            jpaRepository.save(jpaEntity);
        } else {
            throw new DecentralizedPowerPlantRepositoryException(
                    String.format("DK %s konnte nicht aktualisiert werden, da DK nicht gefunden wurde", id.getValue())
            );
        }
    }

    @Override
    public void deleteById(DecentralizedPowerPlantIdVO id) throws DecentralizedPowerPlantRepositoryException {
        Optional<DecentralizedPowerPlant> jpaEntity = jpaRepository.findOneById(id.getValue());
        if (jpaEntity.isPresent()) {
            DecentralizedPowerPlant dpp = jpaEntity.get();

            dpp.getWaters().forEach(waterEnergyJpaRepository::delete);
            dpp.getWinds().forEach(windEnergyJpaRepository::delete);
            dpp.getSolars().forEach(solarEnergyJpaRepository::delete);
            dpp.getOthers().forEach(otherEnergyJpaRepository::delete);
            dpp.getStorages().forEach(storageJpaRepository::delete);

            jpaRepository.delete(dpp);
        } else {
            throw new DecentralizedPowerPlantRepositoryException(
                    String.format("DK %s konnte nicht gelöscht werden, da DK nicht gefunden wurde", id.getValue())
            );
        }
    }


}
