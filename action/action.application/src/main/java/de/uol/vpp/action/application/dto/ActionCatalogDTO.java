package de.uol.vpp.action.application.dto;

import lombok.Data;

import java.util.List;

/**
 * Datentransferobjekt für Handlungsempfehlungskataloge
 */
@Data
public class ActionCatalogDTO {
    private Long startTimestamp;
    private Long endTimestamp;
    private String problemType;
    private Double averageGap;
    private List<ActionDTO> actions;
}
