package de.uol.vpp.masterdata.infrastructure.repositories;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.entities.OtherEnergyEntity;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.exceptions.ProducerRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IOtherEnergyRepository;
import de.uol.vpp.masterdata.domain.valueobjects.OtherEnergyIdVO;
import de.uol.vpp.masterdata.infrastructure.InfrastructureEntityConverter;
import de.uol.vpp.masterdata.infrastructure.entities.DecentralizedPowerPlant;
import de.uol.vpp.masterdata.infrastructure.entities.Household;
import de.uol.vpp.masterdata.infrastructure.entities.OtherEnergy;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.DecentralizedPowerPlantJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.HouseholdJpaRepository;
import de.uol.vpp.masterdata.infrastructure.jpaRepositories.OtherEnergyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementierung der Schnittstellendefinition {@link IOtherEnergyRepository}
 */
@RequiredArgsConstructor
@Service
public class OtherEnergyRepositoryImpl implements IOtherEnergyRepository {

    private final OtherEnergyJpaRepository jpaRepository;
    private final DecentralizedPowerPlantJpaRepository decentralizedPowerPlantJpaRepository;
    private final HouseholdJpaRepository householdJpaRepository;
    private final InfrastructureEntityConverter converter;

    @Override
    public List<OtherEnergyEntity> getAllByDecentralizedPowerPlant(DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        try {
            Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository
                    .findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
            if (dpp.isPresent()) {
                List<OtherEnergyEntity> result = new ArrayList<>();
                for (OtherEnergy otherEnergy : jpaRepository.findAllByDecentralizedPowerPlant(dpp.get())) {
                    result.add(converter.toDomain(otherEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("DK %s konnte nicht gefunden werden um alternative Energieanlagen abzufragen", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<OtherEnergyEntity> getAllByHousehold(HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        try {
            Optional<Household> household = householdJpaRepository
                    .findOneById(householdAggregate.getHouseholdId().getValue());
            if (household.isPresent()) {
                List<OtherEnergyEntity> result = new ArrayList<>();
                for (OtherEnergy OtherEnergy : jpaRepository.findAllByHousehold(household.get())) {
                    result.add(converter.toDomain(OtherEnergy));
                }
                return result;
            } else {
                throw new ProducerRepositoryException(String.format("Haushalt %s konnte nicht gefunden werden um alternative Energieanlagen abzufragen", householdAggregate.getHouseholdId().getValue()));
            }
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }

    }

    @Override
    public Optional<OtherEnergyEntity> getById(OtherEnergyIdVO id) throws ProducerRepositoryException {
        try {
            Optional<OtherEnergy> result = jpaRepository.findOneById(id.getValue());
            if (result.isPresent()) {
                return Optional.of(converter.toDomain(result.get()));
            }
            return Optional.empty();
        } catch (ProducerException e) {
            throw new ProducerRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void save(OtherEnergyEntity OtherEnergyEntity) throws ProducerRepositoryException {
        OtherEnergy jpaEntity = converter.toInfrastructure(OtherEnergyEntity);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public void assignToDecentralizedPowerPlant(OtherEnergyEntity otherEnergyEntity, DecentralizedPowerPlantAggregate decentralizedPowerPlantAggregate) throws ProducerRepositoryException {
        Optional<DecentralizedPowerPlant> dpp = decentralizedPowerPlantJpaRepository.findOneById(decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue());
        if (dpp.isPresent()) {
            Optional<OtherEnergy> other = jpaRepository.findOneById(otherEnergyEntity.getId().getValue());
            if (other.isPresent()) {
                if (other.get().getDecentralizedPowerPlant() == null &&
                        other.get().getHousehold() == null) {
                    other.get().setDecentralizedPowerPlant(dpp.get());
                    jpaRepository.save(other.get());
                    dpp.get().getOthers().add(other.get());
                    decentralizedPowerPlantJpaRepository.save(dpp.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("Alternative Energieanlage %s konnte DK nicht zugewiesen werden, da alternative Energieanlage bereits zugewiesen ist ", otherEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Alternative Energieanlage %s konnte nicht gefunden werden", otherEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("DK %s konnte nicht gefunden werden", decentralizedPowerPlantAggregate.getDecentralizedPowerPlantId().getValue())
            );
        }
    }

    @Override
    public void assignToHousehold(OtherEnergyEntity otherEnergyEntity, HouseholdAggregate householdAggregate) throws ProducerRepositoryException {
        Optional<Household> household = householdJpaRepository.findOneById(householdAggregate.getHouseholdId().getValue());
        if (household.isPresent()) {
            Optional<OtherEnergy> OtherEnergy = jpaRepository.findOneById(otherEnergyEntity.getId().getValue());
            if (OtherEnergy.isPresent()) {
                if (OtherEnergy.get().getDecentralizedPowerPlant() == null &&
                        OtherEnergy.get().getHousehold() == null) {
                    OtherEnergy.get().setHousehold(household.get());
                    jpaRepository.save(OtherEnergy.get());
                    household.get().getOthers().add(OtherEnergy.get());
                    householdJpaRepository.save(household.get());
                } else {
                    throw new ProducerRepositoryException(
                            String.format("Alternative Energieanlage %s konnte Haushalt nicht zugewiesen werden, da alternative Energieanlage bereits zugewiesen ist ", otherEnergyEntity.getId().getValue())
                    );
                }
            } else {
                throw new ProducerRepositoryException(
                        String.format("Alternative Energieanlage %s konnte nicht gefunden werden", otherEnergyEntity.getId().getValue())
                );
            }
        } else {
            throw new ProducerRepositoryException(
                    String.format("DK %s konnte nicht gefunden werden", householdAggregate.getHouseholdId().getValue())
            );
        }
    }

    @Override
    public void deleteById(OtherEnergyIdVO id) throws ProducerRepositoryException {
        Optional<OtherEnergy> jpaEntity = jpaRepository.findOneById(id.getValue());
        if (jpaEntity.isPresent()) {
            jpaRepository.delete(jpaEntity.get());
        } else {
            throw new ProducerRepositoryException(
                    String.format("Alternative Energieanlage %s konnte nicht gelöscht werden, da alternative Energieanlage nicht gefunden wurde", id.getValue())
            );
        }
    }

    @Override
    public void update(OtherEnergyIdVO id, OtherEnergyEntity domainEntity) throws ProducerRepositoryException {
        Optional<OtherEnergy> jpaEntityOptional = jpaRepository.findOneById(id.getValue());
        if (jpaEntityOptional.isPresent()) {
            OtherEnergy jpaEntity = jpaEntityOptional.get();
            OtherEnergy updated = converter.toInfrastructure(domainEntity);
            jpaEntity.setId(updated.getId());
            jpaEntity.setRatedCapacity(updated.getRatedCapacity());
            jpaEntity.setCapacity(updated.getCapacity());
            jpaRepository.save(jpaEntity);
        } else {
            throw new ProducerRepositoryException("Alternative Energieanlage konnte nicht aktualisiert werden, da alternative Energieanlage nicht gefunden wurde");
        }
    }

}
