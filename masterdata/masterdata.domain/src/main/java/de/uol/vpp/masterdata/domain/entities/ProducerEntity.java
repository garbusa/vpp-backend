package de.uol.vpp.masterdata.domain.entities;

import de.uol.vpp.masterdata.domain.architecture.DomainEntity;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerIdVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerPowerVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerStatusVO;
import de.uol.vpp.masterdata.domain.valueobjects.ProducerTypeVO;
import lombok.Data;

@DomainEntity
@Data
public class ProducerEntity {
    private ProducerIdVO producerId;
    private ProducerPowerVO producerPower;
    private ProducerTypeVO producerType;
    private ProducerStatusVO producerStatus;
}
