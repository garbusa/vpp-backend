package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.repositories.HouseholdRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.services.HouseholdServiceException;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HouseholdServiceImpl implements IHouseholdService {

    private final IHouseholdRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;

    @Override
    public List<HouseholdAggregate> getAllByVppId(String vppBusinessKey) throws HouseholdServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional;
            virtualPowerPlantOptional = virtualPowerPlantRepository.getById(new VirtualPowerPlantIdVO(vppBusinessKey));
            if (virtualPowerPlantOptional.isPresent()) {
                return repository.getAllByVppKey(virtualPowerPlantOptional.get());
            }
            throw new HouseholdServiceException(
                    String.format("There is no vpp %s to find any dpp's", vppBusinessKey)
            );
        } catch (HouseholdRepositoryException | VirtualPowerPlantRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }

    }

    @Override
    public HouseholdAggregate get(String businessKey) throws HouseholdServiceException {
        return repository.getById(new HouseholdIdVO(businessKey))
                .orElseThrow(() -> new HouseholdServiceException(String.format("Can't find household by id %s", businessKey)));

    }

    @Override
    public void save(HouseholdAggregate domainEntity, String virtualPowerPlantBusinessKey) throws HouseholdServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional = virtualPowerPlantRepository.getById(
                    new VirtualPowerPlantIdVO(virtualPowerPlantBusinessKey)
            );

            if (virtualPowerPlantOptional.isPresent()) {
                VirtualPowerPlantAggregate virtualPowerPlant = virtualPowerPlantOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, virtualPowerPlant);
            } else {
                throw new HouseholdServiceException(
                        String.format("Failed to assign dpp %s to vpp %s", domainEntity.getHouseholdId().getId(),
                                virtualPowerPlantBusinessKey)
                );
            }
        } catch (HouseholdRepositoryException | VirtualPowerPlantRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey) throws HouseholdServiceException {
        try {
            repository.deleteById(new HouseholdIdVO(businessKey));
        } catch (HouseholdRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }
}
