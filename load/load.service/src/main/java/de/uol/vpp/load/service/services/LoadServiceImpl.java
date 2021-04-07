package de.uol.vpp.load.service.services;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.exceptions.LoadServiceException;
import de.uol.vpp.load.domain.repositories.ILoadRepository;
import de.uol.vpp.load.domain.services.ILoadService;
import de.uol.vpp.load.domain.valueobjects.LoadStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadVirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoadServiceImpl implements ILoadService {

    private final ILoadRepository repository;

    @Override
    public List<LoadAggregate> getVppLoad(String vppBusinessKey, ZonedDateTime startTs) throws LoadServiceException {
        try {
            List<LoadAggregate> loads = repository.getCurrentVppLoads(new LoadVirtualPowerPlantIdVO(vppBusinessKey));
            return loads.stream().filter(loadAggregate -> {
                try {
                    return loadAggregate.getLoadStartTimestamp().isGreater(
                            new LoadStartTimestampVO(startTs.toEpochSecond())
                    );
                } catch (LoadException e) {
                    return false;
                }
            }).collect(Collectors.toList());
        } catch (LoadRepositoryException | LoadException e) {
            throw new LoadServiceException(e.getMessage(), e);
        }
    }
}
