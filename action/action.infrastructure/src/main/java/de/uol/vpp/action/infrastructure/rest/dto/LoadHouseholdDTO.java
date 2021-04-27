package de.uol.vpp.action.infrastructure.rest.dto;

import lombok.Data;

@Data
public class LoadHouseholdDTO {
    private String householdId;
    private Long startTimestamp;
    private Double loadValue;
    private Integer householdMemberAmount;
}
