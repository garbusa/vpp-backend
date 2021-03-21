package de.uol.vpp.masterdata.service.services;

import de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate;
import de.uol.vpp.masterdata.domain.aggregates.VirtualPowerPlantAggregate;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.repositories.HouseholdRepositoryException;
import de.uol.vpp.masterdata.domain.repositories.IHouseholdRepository;
import de.uol.vpp.masterdata.domain.repositories.IVirtualPowerPlantRepository;
import de.uol.vpp.masterdata.domain.repositories.VirtualPowerPlantRepositoryException;
import de.uol.vpp.masterdata.domain.services.HouseholdServiceException;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import de.uol.vpp.masterdata.domain.utils.IPublishUtil;
import de.uol.vpp.masterdata.domain.utils.PublishException;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.VirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = HouseholdServiceException.class)
@RequiredArgsConstructor
@Service
public class HouseholdServiceImpl implements IHouseholdService {

    private final IHouseholdRepository repository;
    private final IVirtualPowerPlantRepository virtualPowerPlantRepository;
    private final IPublishUtil publishUtil;

    @Override
    public List<HouseholdAggregate> getAllByVppId(String vppBusinessKey) throws HouseholdServiceException {
        try {
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional;
            virtualPowerPlantOptional = virtualPowerPlantRepository.getById(new VirtualPowerPlantIdVO(vppBusinessKey));
            if (virtualPowerPlantOptional.isPresent()) {
                return repository.getAllByVirtualPowerPlant(virtualPowerPlantOptional.get());
            }
            throw new HouseholdServiceException(
                    String.format("There is no vpp %s to find any households", vppBusinessKey)
            );
        } catch (HouseholdRepositoryException | VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }

    }

    @Override
    public HouseholdAggregate get(String businessKey) throws HouseholdServiceException {
        try {
            return repository.getById(new HouseholdIdVO(businessKey))
                    .orElseThrow(() -> new HouseholdServiceException(String.format("Can't find household by id %s", businessKey)));
        } catch (HouseholdException | HouseholdRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void save(HouseholdAggregate domainEntity, String virtualPowerPlantBusinessKey) throws HouseholdServiceException {
        try {
            if (repository.getById(domainEntity.getHouseholdId()).isPresent()) {
                throw new HouseholdServiceException(
                        String.format("household with id %s already exists", domainEntity.getHouseholdId().getId()));
            }
            Optional<VirtualPowerPlantAggregate> virtualPowerPlantOptional = virtualPowerPlantRepository.getById(
                    new VirtualPowerPlantIdVO(virtualPowerPlantBusinessKey)
            );

            if (virtualPowerPlantOptional.isPresent()) {
                VirtualPowerPlantAggregate virtualPowerPlant = virtualPowerPlantOptional.get();
                repository.save(domainEntity);
                repository.assign(domainEntity, virtualPowerPlant);
            } else {
                throw new HouseholdServiceException(
                        String.format("Failed to assign household %s to vpp %s", domainEntity.getHouseholdId().getId(),
                                virtualPowerPlantBusinessKey)
                );
            }
        } catch (HouseholdRepositoryException | VirtualPowerPlantRepositoryException | VirtualPowerPlantException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String businessKey, String vppBusinessKey) throws HouseholdServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new HouseholdIdVO(businessKey))) {
                repository.deleteById(new HouseholdIdVO(businessKey));
            } else {
                throw new HouseholdServiceException("failed to delete household. vpp has to be unpublished.");
            }

        } catch (HouseholdRepositoryException | HouseholdException | VirtualPowerPlantException | PublishException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String businessKey, HouseholdAggregate domainEntity, String vppBusinessKey) throws HouseholdServiceException {
        try {
            if (publishUtil.isEditable(new VirtualPowerPlantIdVO(vppBusinessKey), new HouseholdIdVO(businessKey))) {
                repository.update(new HouseholdIdVO(businessKey), domainEntity);
            }
        } catch (PublishException | VirtualPowerPlantException | HouseholdException | HouseholdRepositoryException e) {
            throw new HouseholdServiceException(e.getMessage(), e);
        }
    }
}
