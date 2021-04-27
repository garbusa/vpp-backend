package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DecentralizedPowerPlantDTO {
    private String decentralizedPowerPlantId;
    private List<SolarEnergyDTO> solars = new ArrayList<>();
    private List<WindEnergyDTO> winds = new ArrayList<>();
    private List<WaterEnergyDTO> waters = new ArrayList<>();
    private List<OtherEnergyDTO> others = new ArrayList<>();
    private List<StorageDTO> storages = new ArrayList<>();
}
