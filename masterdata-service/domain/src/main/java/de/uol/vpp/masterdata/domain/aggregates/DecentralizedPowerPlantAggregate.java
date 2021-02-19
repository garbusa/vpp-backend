package de.uol.vpp.masterdata.domain.aggregates;

import de.uol.vpp.masterdata.domain.architecture.Aggregate;
import de.uol.vpp.masterdata.domain.entities.ProducerEntity;
import de.uol.vpp.masterdata.domain.entities.StorageEntity;
import de.uol.vpp.masterdata.domain.valueobjects.DecentralizedPowerPlantIdVO;
import lombok.Data;

import java.util.List;

@Aggregate
@Data
public class DecentralizedPowerPlantAggregate {
    private DecentralizedPowerPlantIdVO decentralizedPowerPlantId;
    private List<ProducerEntity> producers;
    private List<StorageEntity> storages;

}
