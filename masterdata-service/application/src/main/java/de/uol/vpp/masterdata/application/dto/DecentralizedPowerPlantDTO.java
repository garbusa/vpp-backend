package de.uol.vpp.masterdata.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class DecentralizedPowerPlantDTO {
    private String decentralizedPowerPlantId;
    @JsonIgnore
    private List<ProducerDTO> producers;
    @JsonIgnore
    private List<StorageDTO> storages;
}
