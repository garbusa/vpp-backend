package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.services.IVirtualPowerPlantService;
import de.uol.vpp.masterdata.domain.services.VirtualPowerPlantServiceException;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VirtualPowerPlantServiceImpl implements IVirtualPowerPlantService {

    private final IVirtualPowerPlantRepository repository;

    @Override
    public List<VirtualPowerPlantAggregate> getAll() throws VirtualPowerPlantServiceException {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }

    @Override
    public VirtualPowerPlantAggregate get(String businessKey) throws VirtualPowerPlantServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> result = repository.getById(new VirtualPowerPlantIdVO(businessKey));
            return result.orElseThrow(() -> new VirtualPowerPlantServiceException(
                    String.format("Can not find VPP by id %s", businessKey)
            ));
        } catch (VirtualPowerPlantRepositoryException e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void save(VirtualPowerPlantAggregate domainEntity) throws VirtualPowerPlantServiceException {
        try {
            repository.save(domainEntity);
        } catch (Exception e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void delete(String businessKey) throws VirtualPowerPlantServiceException {
        try {
            repository.deleteById(new VirtualPowerPlantIdVO(businessKey));
        } catch (Exception e) {
            throw new VirtualPowerPlantServiceException(e.getMessage(), e);
        }
    }
}
