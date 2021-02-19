package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

@Data
public class ProducerDTO {
    private String producerId;
    private Double ratedPower;
    private Double height;
    private Double width;
    private Double length;
    private Double weight;
    private String productType;
    private String energyType;
}
