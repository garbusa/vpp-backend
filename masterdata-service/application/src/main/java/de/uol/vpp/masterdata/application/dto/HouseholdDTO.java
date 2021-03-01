package de.uol.vpp.masterdata.application.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HouseholdDTO {
    private String householdId;
    private List<ProducerDTO> producers = new ArrayList<>();
    private List<StorageDTO> storages = new ArrayList<>();
    private List<ConsumerDTO> consumers = new ArrayList<>();
    private Integer householdMemberAmount;
}
