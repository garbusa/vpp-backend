package de.uol.vpp.masterdata.application.payload;

import lombok.Data;

@Data
public class StorageStatusUpdateRequest {
    private String businessKey;
    private Double capacity;
    private String vppBusinessKey;
}
