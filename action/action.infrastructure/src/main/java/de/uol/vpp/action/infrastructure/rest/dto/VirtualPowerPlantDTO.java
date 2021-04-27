package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

@Data
public class VirtualPowerPlantDTO {
    private String virtualPowerPlantId;
    /*    @JsonIgnore
        private List<HouseholdDTO> households = new ArrayList<>();
        @JsonIgnore
        private List<DecentralizedPowerPlantDTO> decentralizedPowerPlants = new ArrayList<>();*/
    private boolean published;
    private Double shortageThreshold;
    private Double overflowThreshold;
}
