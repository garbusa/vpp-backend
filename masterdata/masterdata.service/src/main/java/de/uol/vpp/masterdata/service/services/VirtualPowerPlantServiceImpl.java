package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.abstracts.DomainHasProducersAndStorages;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.services.IVirtualPowerPlantService;
import de.uol.vpp.masterdata.domain.services.VirtualPowerPlantServiceException;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantPublishedVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Transactional(rollbackFor = VirtualPowerPlantServiceException.class)
@RequiredArgsConstructor
@Service
public class VirtualPowerPlantServiceImpl implements IVirtualPowerPlantService {

    private final IVirtualPowerPlantRepository repository;

    @Override
    public List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantServiceException {
        try {
            return repository.getAll();
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<VirtualPowerPlantAggregate> getAllActives() throws VirtualPowerPlantServiceException {
        try {
            return repository.getAll().stream().filter((vpp) -> vpp.getPublished().isValue()).collect(Collectors.toList());
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException {
        try {
            if (repository.getById(domainEntity.getVirtualPowerPlantId()).isPresent()) {
                throw new VirtualPowerPlantServiceException(
                        String.format("VK %s existiert bereits", domainEntity.getVirtualPowerPlantId().getValue()));
            }
            repository.save(domainEntity);
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void delete(String virtualPowerPlantId) throws VirtualPowerPlantServiceException {
        try {
            if (!repository.isPublished(new VirtualPowerPlantIdVO(virtualPowerPlantId))) {
                repository.deleteById(new VirtualPowerPlantIdVO(virtualPowerPlantId));
            } else {
                throw new VirtualPowerPlantServiceException(
                        String.format("VK %s konnte nicht gelöscht werden, da VK veröffentlich ist", virtualPowerPlantId)
                );
            }

        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void publish(String virtualPowerPlantId) throws VirtualPowerPlantServiceException {
        try {
            if (!repository.isPublished(new VirtualPowerPlantIdVO(virtualPowerPlantId))) {
                VirtualPowerPlantAggregate vpp = this.get(virtualPowerPlantId);
                if (vpp.getHouseholds().size() > 0) {
                    AtomicBoolean hasProducer = new AtomicBoolean(false);
                    vpp.getHouseholds().forEach((household) -> {
                        checkProducers(hasProducer, household);
                    });
                    if (!hasProducer.get() && vpp.getDecentralizedPowerPlants().size() > 0) {
                        vpp.getDecentralizedPowerPlants().forEach((dpp) -> {
                            checkProducers(hasProducer, dpp);
                        });
                    }
                    if (!hasProducer.get()) {
                        throw new VirtualPowerPlantServiceException(
                                String.format("VK %s konnte nicht veröffentlicht werden. VK benötigt min. eine Erzeugungsanlage", virtualPowerPlantId)
                        );
                    }
                    repository.publish(new VirtualPowerPlantIdVO(virtualPowerPlantId));
                } else {
                    throw new VirtualPowerPlantServiceException(
                            String.format("VK %s konnte nicht veröffentlicht werden. VK benötigt min. ein Haushalt", virtualPowerPlantId)
                    );
                }
            } else {
                throw new VirtualPowerPlantServiceException(
                        String.format("VK %s ist bereits veröffentlicht", virtualPowerPlantId)
                );
            }
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public VirtualPowerPlantAggregate get(String virtualPowerPlantId) throws VirtualPowerPlantServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> result = repository.getById(new VirtualPowerPlantIdVO(virtualPowerPlantId));
            return result.orElseThrow(() -> new VirtualPowerPlantServiceException(
                    String.format("VK %s konnte nicht gefunden werden", virtualPowerPlantId)
            ));
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }

    }

    private void checkProducers(AtomicBoolean hasProducer, DomainHasProducersAndStorages householdOrDpp) {
        if (householdOrDpp.getWaters().size() > 0) {
            hasProducer.set(true);
        }
        if (householdOrDpp.getSolars().size() > 0) {
            hasProducer.set(true);
        }
        if (householdOrDpp.getWinds().size() > 0) {
            hasProducer.set(true);
        }
        if (householdOrDpp.getOthers().size() > 0) {
            hasProducer.set(true);
        }
    }

    @Override
    public void unpublish(String virtualPowerPlantId) throws VirtualPowerPlantServiceException {
        try {
            VirtualPowerPlantAggregate vpp = this.get(virtualPowerPlantId);
            if (vpp.getPublished().isValue()) {
                vpp.setPublished(
                        new VirtualPowerPlantPublishedVO(false)
                );
                repository.unpublish(new VirtualPowerPlantIdVO(virtualPowerPlantId));
            } else {
                throw new VirtualPowerPlantServiceException(
                        String.format("VK %s ist bereits unveröffentlicht", virtualPowerPlantId)
                );
            }
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String virtualPowerPlantId, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException {
        try {
            VirtualPowerPlantAggregate vpp = this.get(virtualPowerPlantId);
            if (!vpp.getPublished().isValue()) {
                if (domainEntity.getVirtualPowerPlantId().getValue().equals(virtualPowerPlantId)) {
                    repository.update(new VirtualPowerPlantIdVO(virtualPowerPlantId), domainEntity);
                } else {
                    throw new VirtualPowerPlantServiceException(
                            "Der Name eines VK kann nicht geändert werden"
                    );
                }
            } else {
                throw new VirtualPowerPlantServiceException(
                        String.format("VK %s konnte nicht aktualisiert werden, da VK veröffentlicht ist", virtualPowerPlantId)
                );
            }
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

}
