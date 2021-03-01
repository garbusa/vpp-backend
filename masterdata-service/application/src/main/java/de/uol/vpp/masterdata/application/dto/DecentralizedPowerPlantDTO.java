package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DecentralizedPowerPlantDTO {
    private String decentralizedPowerPlantId;
    private List<ProducerDTO> producers = new ArrayList<>();
    private List<StorageDTO> storages = new ArrayList<>();
}
