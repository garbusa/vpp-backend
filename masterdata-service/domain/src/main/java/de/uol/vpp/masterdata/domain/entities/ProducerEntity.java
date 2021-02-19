package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.architecture.DomainEntity;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerDimensionVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerPowerVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerTypeVO;
import lombok.Data;

@DomainEntity
@Data
public class ProducerEntity {
    private ProducerIdVO producerId;
    private ProducerPowerVO producerPower;
    private ProducerDimensionVO producerDimension;
    private ProducerTypeVO producerType;
}
