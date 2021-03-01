package de.uol.vpp.masterdata.application.payload;

import lombok.Data;

@Data
public class ProducerStatusUpdateRequest {
    private String businessKey;
    private boolean isRunning;
    private Double capacity;
    private String vppBusinessKey;
}
