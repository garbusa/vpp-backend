package de.uol.vpp.load.infrastructure.jpaRepositories;

import de.uol.vpp.load.infrastructure.entities.ELoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface LoadJpaRepository extends JpaRepository<ELoad, ELoad.VppTimestamp> {
    List<ELoad> findAllByVppTimestampVppBusinessKeyAndOutdatedAndForecasted(String vppBusinessKey, boolean outdated, boolean forecasted);

    List<ELoad> findAllByVppTimestampVppBusinessKeyAndOutdated(String vppBusinessKey, boolean outdated);

    List<ELoad> findAllByVppTimestamp_VppBusinessKeyAndVppTimestamp_StartTimestampLessThanEqual(String vppBusinessKey, ZonedDateTime compare);
}
