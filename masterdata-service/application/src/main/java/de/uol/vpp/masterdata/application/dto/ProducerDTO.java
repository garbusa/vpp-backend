package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

@Data
public class ProducerDTO {
    private String producerId;
    private Double ratedPower;
    private String productType;
    private String energyType;
    private boolean isRunning;
    private Double capacity;
}
