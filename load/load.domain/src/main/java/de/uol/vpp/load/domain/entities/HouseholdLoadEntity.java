package de.uol.vpp.load.domain.entities;

import de.uol.vpp.load.domain.valueobjects.HouseholdLoadCurrentValueVO;
import de.uol.vpp.load.domain.valueobjects.HouseholdLoadForecastedValueVO;
import de.uol.vpp.load.domain.valueobjects.HouseholdLoadHouseholdIdVo;
import de.uol.vpp.load.domain.valueobjects.HouseholdLoadStartTimestampVo;
import lombok.Data;

import java.util.List;

@Data
public class HouseholdLoadEntity {
    private HouseholdLoadHouseholdIdVo householdLoadHouseholdId;
    private HouseholdLoadStartTimestampVo householdLoadStartTimestampVo;
    private HouseholdLoadCurrentValueVO householdLoadCurrentValueVO;
    private List<HouseholdLoadForecastedValueVO> householdLoadForecastedValues;
}
