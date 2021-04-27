package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HouseholdDTO {
    private String householdId;
    private List<SolarEnergyDTO> solars = new ArrayList<>();
    private List<WindEnergyDTO> winds = new ArrayList<>();
    private List<WaterEnergyDTO> waters = new ArrayList<>();
    private List<OtherEnergyDTO> others = new ArrayList<>();
    private List<StorageDTO> storages = new ArrayList<>();
    private Integer householdMemberAmount;
}
