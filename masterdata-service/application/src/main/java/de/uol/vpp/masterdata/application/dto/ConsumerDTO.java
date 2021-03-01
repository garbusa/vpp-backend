package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

@Data
public class ConsumerDTO {
    private String consumerId;
    private Double consumingPower;
    private boolean isRunning;
}
