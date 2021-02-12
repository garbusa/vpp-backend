package de.uol.vpp.masterdata.domain.dpp;

import de.uol.vpp.masterdata.domain.producer.ProducerEntity;
import de.uol.vpp.masterdata.domain.storage.StorageEntity;
import de.uol.vpp.masterdata.domain.architecture.Aggregate;
import lombok.Data;

import java.util.List;

@Aggregate
@Data
public class DecentralizedPowerPlantAggregate {
    private DecentralizedPowerPlantId decentralizedPowerPlantId;
    private List<ProducerEntity> producers;
    private List<StorageEntity> storages;
}
