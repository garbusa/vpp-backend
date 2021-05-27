package de.uol.vpp.load.application.dto;

import lombok.Data;

/**
 * Datentransferobjekt für Lastenwerte für Haushalt an einem Zeitstempel
 */
@Data
public class LoadHouseholdDTO {
    private String householdId;
    private Long startTimestamp;
    private Double loadValue;
    private Integer householdMemberAmount;
}
