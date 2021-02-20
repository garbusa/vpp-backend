package de.uol.vpp.masterdata.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class VirtualPowerPlantDTO {
    private String virtualPowerPlantId;
    @JsonIgnore
    private List<HouseholdDTO> households;
    @JsonIgnore
    private List<DecentralizedPowerPlantDTO> decentralizedPowerPlants;
}
