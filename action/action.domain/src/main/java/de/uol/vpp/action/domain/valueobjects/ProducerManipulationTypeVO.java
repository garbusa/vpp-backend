package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.ProducerManipulationTypeEnum;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import lombok.Data;

@Data
public class ProducerManipulationTypeVO {
    private ProducerManipulationTypeEnum value;

    public ProducerManipulationTypeVO(ProducerManipulationTypeEnum value) throws ManipulationException {
        if (value == null || (!value.equals(ProducerManipulationTypeEnum.PRODUCER_UP) &&
                !value.equals(ProducerManipulationTypeEnum.PRODUCER_DOWN))) {
            throw new ManipulationException("manipulationType", "ProducerManipulation");
        }
        this.value = value;
    }
}
