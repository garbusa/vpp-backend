package de.uol.vpp.load.infrastructure.jpaRepositories;

import de.uol.vpp.load.infrastructure.entities.ELoad;
import de.uol.vpp.load.infrastructure.entities.ELoadHousehold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoadHouseholdJpaRepository extends JpaRepository<ELoadHousehold, ELoadHousehold.HouseholdTimestamp> {
    List<ELoadHousehold> findAllByLoad(ELoad load);

    void deleteAllByLoad(ELoad load);
}
