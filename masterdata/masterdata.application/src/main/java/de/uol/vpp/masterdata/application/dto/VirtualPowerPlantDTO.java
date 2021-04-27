package de.uol.vpp.masterdata.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VirtualPowerPlantDTO {
    private String virtualPowerPlantId;
    @JsonIgnore
    private List<HouseholdDTO> households = new ArrayList<>();
    @JsonIgnore
    private List<DecentralizedPowerPlantDTO> decentralizedPowerPlants = new ArrayList<>();
    private boolean published;
    private Double shortageThreshold;
    private Double overflowThreshold;
}
