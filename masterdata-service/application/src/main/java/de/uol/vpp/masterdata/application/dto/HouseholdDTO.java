package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class HouseholdDTO {
    private String householdId;
    private List<ProducerDTO> producers;
    private List<StorageDTO> storages;
    private List<ConsumerDTO> consumers;
    private Integer householdMemberAmount;
}
