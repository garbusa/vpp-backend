package de.uol.vpp.masterdata.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class HouseholdDTO {
    private String householdId;
    @JsonIgnore
    private List<ProducerDTO> producers;
    @JsonIgnore
    private List<StorageDTO> storages;
    @JsonIgnore
    private List<ConsumerDTO> consumers;
    private Integer householdMemberAmount;
}
