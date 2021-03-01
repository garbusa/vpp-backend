package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VirtualPowerPlantDTO {
    private String virtualPowerPlantId;
    private List<HouseholdDTO> households = new ArrayList<>();
    private List<DecentralizedPowerPlantDTO> decentralizedPowerPlants = new ArrayList<>();
    private boolean published;
}
