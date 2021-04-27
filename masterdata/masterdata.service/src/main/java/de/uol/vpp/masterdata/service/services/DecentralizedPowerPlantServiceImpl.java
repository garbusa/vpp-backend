package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.DecentralizedPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.services.DecentralizedPowerPlantServiceException;
import de.uol.vpp.masterdata.domain.services.IDecentralizedPowerPlantService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = DecentralizedPowerPlantServiceException.class)
@RequiredArgsConstructor
@Service
public class DecentralizedPowerPlantServiceImpl implements IDecentralizedPowerPlantService {

    private final IDecentralizedPowerPlantRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<DecentralizedPowerPlantAggregate> getAllByVppId(String vppBusinessKey) throws DecentralizedPowerPlantServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional;
            virtualPowerPlantOptional = virtualPowerPlantRepository.getById(new VirtualPowerPlantIdVO(vppBusinessKey));
            if (virtualPowerPlantOptional.isPresent()) {
                return repository.getAllByVppKey(virtualPowerPlantOptional.get());
            }
            throw new DecentralizedPowerPlantServiceException(
                    String.format("There is no vpp %s to find any dpp's", vppBusinessKey)
            );
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public DecentralizedPowerPlantAggregate get(String businessKey) throws DecentralizedPowerPlantServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = repository.getById(new DecentralizedPowerPlantIdVO(businessKey));
            if (dpp.isPresent()) {
                return dpp.get();
            } else {
                throw new DecentralizedPowerPlantServiceException(String.format("Can't find DPP by actionRequestId %s", businessKey));
            }
        } catch (DecentralizedPowerPlantRepositoryException | DecentralizedPowerPlantException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void save(DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantBusinessKey) throws DecentralizedPowerPlantServiceException {
        try {
            if (repository.getById(domainEntity.getDecentralizedPowerPlantId()).isPresent()) {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("dpp with actionRequestId %s already exists", domainEntity.getDecentralizedPowerPlantId().getValue()));
            }
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional = virtualPowerPlantRepository.getById(
                    new VirtualPowerPlantIdVO(virtualPowerPlantBusinessKey)
            );
            if (virtualPowerPlantOptional.isPresent()) {
                VirtualPowerPlantAggregate virtualPowerPlant = virtualPowerPlantOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, virtualPowerPlant);
            } else {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("Failed to assign dpp %s to vpp %s", domainEntity.getDecentralizedPowerPlantId().getValue(),
                                virtualPowerPlantBusinessKey)
                );
            }
        } catch (DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey, String vppBusinessKey) throws DecentralizedPowerPlantServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey),
                    new DecentralizedPowerPlantIdVO(businessKey))) {
                repository.deleteById(new DecentralizedPowerPlantIdVO(businessKey));
            } else {
                throw new DecentralizedPowerPlantServiceException("deleting dpp failed. vpp has to be unpublished");
            }
        } catch (DecentralizedPowerPlantRepositoryException | DecentralizedPowerPlantException | VirtualPowerPlantException | PublishException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String businessKey, DecentralizedPowerPlantAggregate domainEntity, String vppBusinessKey) throws DecentralizedPowerPlantServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new DecentralizedPowerPlantIdVO(businessKey))) {
                repository.update(new DecentralizedPowerPlantIdVO(businessKey), domainEntity);
            }
        } catch (PublishException | VirtualPowerPlantException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }


}
