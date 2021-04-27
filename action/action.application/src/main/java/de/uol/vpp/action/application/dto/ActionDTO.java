package de.uol.vpp.action.application.dto;

import lombok.Data;

@Data
public class ActionDTO {
    private String actionType;
    private String producerOrStorageId;
    private Boolean isStorage;
    private Double actionValue;
    private Double hours;
}
