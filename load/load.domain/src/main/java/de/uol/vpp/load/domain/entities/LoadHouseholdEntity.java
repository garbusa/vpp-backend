package de.uol.vpp.load.domain.entities;

import de.uol.vpp.load.domain.valueobjects.LoadHouseholdIdVO;
import de.uol.vpp.load.domain.valueobjects.LoadHouseholdMemberAmountVO;
import de.uol.vpp.load.domain.valueobjects.LoadHouseholdStartTimestampVO;
import de.uol.vpp.load.domain.valueobjects.LoadHouseholdValueVO;
import lombok.Data;

@Data
public class LoadHouseholdEntity {
    private LoadHouseholdIdVO loadHouseholdId;
    private LoadHouseholdStartTimestampVO loadHouseholdStartTimestamp;

    private LoadHouseholdMemberAmountVO loadHouseholdMemberAmount;
    private LoadHouseholdValueVO loadHouseholdValueVO;
}
