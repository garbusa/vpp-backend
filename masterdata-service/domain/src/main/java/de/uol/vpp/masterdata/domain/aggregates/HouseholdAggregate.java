package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.architecture.Aggregate;
import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.HouseholdMemberAmountVO;
import lombok.Data;

import java.util.List;

@Aggregate
@Data
public class HouseholdAggregate {
    private HouseholdIdVO householdId;
    private List<ProducerEntity> producers;
    private List<StorageEntity> storages;
    private List<ConsumerEntity> consumers;
    private HouseholdMemberAmountVO householdMemberAmount;
}
