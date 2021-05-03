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
    public List<DecentralizedPowerPlantAggregate> getAllByVppId(String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional;
            virtualPowerPlantOptional = virtualPowerPlantRepository.getById(new VirtualPowerPlantIdVO(virtualPowerPlantId));
            if (virtualPowerPlantOptional.isPresent()) {
                return repository.getAllByVppKey(virtualPowerPlantOptional.get());
            }
            throw new DecentralizedPowerPlantServiceException(
                    String.format("VK %s konnte nicht gefunden werden um DKs abzufragen", virtualPowerPlantId)
            );
        } catch (VirtualPowerPlantRepositoryException | VirtualPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public DecentralizedPowerPlantAggregate get(String decentralizedPowerPlantId) throws DecentralizedPowerPlantServiceException {
        try {
            Optional<DecentralizedPowerPlantAggregate> dpp = repository.getById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            if (dpp.isPresent()) {
                return dpp.get();
            } else {
                throw new DecentralizedPowerPlantServiceException(String.format("DK %s konnte nicht gefunden werden", decentralizedPowerPlantId));
            }
        } catch (DecentralizedPowerPlantRepositoryException | DecentralizedPowerPlantException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void save(DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException {
        try {
            if (repository.getById(domainEntity.getDecentralizedPowerPlantId()).isPresent()) {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("DK %s existiert bereits", domainEntity.getDecentralizedPowerPlantId().getValue()));
            }
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional = virtualPowerPlantRepository.getById(
                    new VirtualPowerPlantIdVO(virtualPowerPlantId)
            );
            if (virtualPowerPlantOptional.isPresent()) {

                if (!publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId),
                        domainEntity.getDecentralizedPowerPlantId())) {
                    throw new DecentralizedPowerPlantServiceException(
                            String.format("DK %s konnte nicht gespeichert werden, da VK %s veröffentlicht ist", domainEntity.getDecentralizedPowerPlantId().getValue(),
                                    virtualPowerPlantId)
                    );
                }

                VirtualPowerPlantAggregate virtualPowerPlant = virtualPowerPlantOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, virtualPowerPlant);
            } else {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("DK %s konnte VK %s nicht zugewiesen werden, da DK bereits zugewiesen wurde", domainEntity.getDecentralizedPowerPlantId().getValue(),
                                virtualPowerPlantId)
                );
            }
        } catch (DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException | VirtualPowerPlantException | PublishException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String decentralizedPowerPlantId, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId),
                    new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId))) {
                repository.deleteById(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId));
            } else {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("DK %s konnte nicht gelöscht werden, da VK veröffentlicht ist", decentralizedPowerPlantId)
                );
            }
        } catch (DecentralizedPowerPlantRepositoryException | DecentralizedPowerPlantException | VirtualPowerPlantException | PublishException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String decentralizedPowerPlantId, DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantId) throws DecentralizedPowerPlantServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(virtualPowerPlantId), new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId))) {
                repository.update(new DecentralizedPowerPlantIdVO(decentralizedPowerPlantId), domainEntity);
            } else {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("DK %s konnte nicht bearbeitet werden, da VK veröffentlicht ist", decentralizedPowerPlantId)
                );
            }
        } catch (PublishException | VirtualPowerPlantException | DecentralizedPowerPlantException | DecentralizedPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }


}
