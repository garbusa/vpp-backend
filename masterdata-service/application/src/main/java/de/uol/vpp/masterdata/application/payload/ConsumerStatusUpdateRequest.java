package de.uol.vpp.masterdata.application.payload;

import lombok.Data;

@Data
public class ConsumerStatusUpdateRequest {
    private String businessKey;
    private boolean isRunning;
    private String vppBusinessKey;
}
