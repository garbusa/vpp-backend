package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
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
    public void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException {
        try {
            if (repository.getById(domainEntity.getVirtualPowerPlantId()).isPresent()) {
                throw new VirtualPowerPlantServiceException(
                        String.format("vpp with actionRequestId %s already exists", domainEntity.getVirtualPowerPlantId().getValue()));
            }
            repository.save(domainEntity);
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void delete(String businessKey) throws VirtualPowerPlantServiceException {
        try {
            if (!repository.isPublished(new VirtualPowerPlantIdVO(businessKey))) {
                repository.deleteById(new VirtualPowerPlantIdVO(businessKey));
            } else {
                throw new VirtualPowerPlantServiceException(
                        String.format("vpp %s can only be edited if its not published", businessKey)
                );
            }

        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void publish(String businessKey) throws VirtualPowerPlantServiceException {
        try {
            if (!repository.isPublished(new VirtualPowerPlantIdVO(businessKey))) {
                VirtualPowerPlantAggregate vpp = this.get(businessKey);
                if (vpp.getHouseholds().size() > 0) {
                    AtomicBoolean hasProducer = new AtomicBoolean(false);
                    vpp.getHouseholds().forEach((household) -> {
                        if (household.getWaters().size() > 0) {
                            hasProducer.set(true);
                        }
                        if (household.getSolars().size() > 0) {
                            hasProducer.set(true);
                        }
                        if (household.getWaters().size() > 0) {
                            hasProducer.set(true);
                        }
                    });
                    if (!hasProducer.get() && vpp.getDecentralizedPowerPlants().size() > 0) {
                        vpp.getDecentralizedPowerPlants().forEach((dpp) -> {
                            if (dpp.getWaters().size() > 0) {
                                hasProducer.set(true);
                            }
                            if (dpp.getSolars().size() > 0) {
                                hasProducer.set(true);
                            }
                            if (dpp.getWinds().size() > 0) {
                                hasProducer.set(true);
                            }
                        });
                    }
                    if (!hasProducer.get()) {
                        throw new VirtualPowerPlantServiceException("failed to publish vpp. a vpp needs minimum one producer in household or dpp");
                    }
                    repository.publish(new VirtualPowerPlantIdVO(businessKey));
                } else {
                    throw new VirtualPowerPlantServiceException("failed to publish vpp. a vpp needs minimum one household");
                }
            } else {
                throw new VirtualPowerPlantServiceException(
                        String.format("vpp %s can only be published when its unpublished", businessKey)
                );
            }
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public VirtualPowerPlantAggregate get(String businessKey) throws VirtualPowerPlantServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> result = repository.getById(new VirtualPowerPlantIdVO(businessKey));
            return result.orElseThrow(() -> new VirtualPowerPlantServiceException(
                    String.format("Can not find VPP by actionRequestId %s", businessKey)
            ));
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void unpublish(String businessKey) throws VirtualPowerPlantServiceException {
        try {
            VirtualPowerPlantAggregate vpp = this.get(businessKey);
            if (vpp.getPublished().isValue()) {
                vpp.setPublished(
                        new VirtualPowerPlantPublishedVO(false)
                );
                repository.unpublish(new VirtualPowerPlantIdVO(businessKey));
            } else {
                throw new VirtualPowerPlantServiceException("vpp has to be published while unpublishing");
            }
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String businessKey, VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException {
        try {
            VirtualPowerPlantAggregate vpp = this.get(businessKey);
            if (!vpp.getPublished().isValue()) {
                repository.update(new VirtualPowerPlantIdVO(businessKey), domainEntity);
            } else {
                throw new VirtualPowerPlantServiceException("failed to update vpp. vpp has to be unpublished");
            }
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

}
