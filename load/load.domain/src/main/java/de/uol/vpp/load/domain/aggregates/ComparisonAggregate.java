package de.uol.vpp.load.domain.aggregates;

import de.uol.vpp.load.domain.entities.ComparisonHouseholdEntity;
import de.uol.vpp.load.domain.valueobjects.ComparisonCurrentTimestampVO;
import de.uol.vpp.load.domain.valueobjects.ComparisonHolidayVO;
import de.uol.vpp.load.domain.valueobjects.ComparisonVirtualPowerPlantIdVO;
import de.uol.vpp.load.domain.valueobjects.ComparisonWeekendVO;
import lombok.Data;

import java.util.List;

@Data
public class ComparisonAggregate {
    private ComparisonVirtualPowerPlantIdVO comparisonVirtualPowerPlantIdVO;
    private ComparisonCurrentTimestampVO comparisonCurrentTimestampVO;

    private ComparisonHolidayVO comparisonHolidayVO;
    private ComparisonWeekendVO comparisonWeekendVO;
    private List<ComparisonHouseholdEntity> comparisonHouseholdVO;
}
