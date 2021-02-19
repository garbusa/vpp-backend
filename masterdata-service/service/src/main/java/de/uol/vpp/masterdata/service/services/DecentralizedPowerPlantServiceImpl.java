package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.DecentralizedPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.repositories.DecentralizedPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IDecentralizedPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.services.DecentralizedPowerPlantServiceException;
import de.uol.vpp.masterdata.domain.services.IDecentralizedPowerPlantService;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DecentralizedPowerPlantServiceImpl implements IDecentralizedPowerPlantService {

    private final IDecentralizedPowerPlantRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;

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
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }

    }

    @Override
    public DecentralizedPowerPlantAggregate get(String businessKey) throws DecentralizedPowerPlantServiceException {
        return repository.getById(new DecentralizedPowerPlantIdVO(businessKey))
                .orElseThrow(() -> new DecentralizedPowerPlantServiceException(String.format("Can't find DPP by id %s", businessKey)));
    }

    @Override
    public void save(DecentralizedPowerPlantAggregate domainEntity, String virtualPowerPlantBusinessKey) throws DecentralizedPowerPlantServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional = virtualPowerPlantRepository.getById(
                    new VirtualPowerPlantIdVO(virtualPowerPlantBusinessKey)
            );

            if (virtualPowerPlantOptional.isPresent()) {
                VirtualPowerPlantAggregate virtualPowerPlant = virtualPowerPlantOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, virtualPowerPlant);
            } else {
                throw new DecentralizedPowerPlantServiceException(
                        String.format("Failed to assign dpp %s to vpp %s", domainEntity.getDecentralizedPowerPlantId().getId(),
                                virtualPowerPlantBusinessKey)
                );
            }
        } catch (DecentralizedPowerPlantRepositoryException | VirtualPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey) throws DecentralizedPowerPlantServiceException {
        try {
            repository.deleteById(new DecentralizedPowerPlantIdVO(businessKey));
        } catch (DecentralizedPowerPlantRepositoryException e) {
            throw new DecentralizedPowerPlantServiceException(e.getMessage(), e);
        }
    }
}
