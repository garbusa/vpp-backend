package de.uol.vpp.action.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActionCatalogDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private String problemType;
    private Double cumulativeGap;
    private List<ActionDTO> actions;
}
