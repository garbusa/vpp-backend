package de.uol.vpp.masterdata.domain.household;

import de.uol.vpp.masterdata.domain.producer.ProducerEntity;
import de.uol.vpp.masterdata.domain.storage.StorageEntity;
import de.uol.vpp.masterdata.domain.architecture.Aggregate;
import de.uol.vpp.masterdata.domain.consumer.ConsumerEntity;
import lombok.Data;

import java.util.List;

@Aggregate
@Data
public class HouseholdAggregate {
    private HouseholdId householdId;
    private List<ProducerEntity> producers;
    private List<StorageEntity> storages;
    private List<ConsumerEntity> consumers;
    private HouseholdMemberAmount householdMemberAmount;
}
