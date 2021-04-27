package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.entities.*;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdMemberAmountVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HouseholdAggregate {
    private HouseholdIdVO householdId;
    private List<SolarEnergyEntity> solars = new ArrayList<>();
    private List<WindEnergyEntity> winds = new ArrayList<>();
    private List<WaterEnergyEntity> waters = new ArrayList<>();
    private List<OtherEnergyEntity> others = new ArrayList<>();
    private List<StorageEntity> storages = new ArrayList<>();
    private HouseholdMemberAmountVO householdMemberAmount;
}
