package de.uol.vpp.load.application.dto;

import lombok.Data;

@Data
public class LoadHouseholdDTO {
    private String householdId;
    private Long startTimestamp;
    private Double loadValue;
    private Integer householdMemberAmount;
}
