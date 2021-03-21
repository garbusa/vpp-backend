package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class ProducerStatusVO {

    private final boolean isRunning;
    private final Double capacity;

    public ProducerStatusVO(boolean isRunning, Double capacity) throws ProducerException {
        if (capacity == null || capacity < 0 || capacity > 100) {
            throw new ProducerException("validation for producer status failed");
        }
        this.isRunning = isRunning;
        this.capacity = capacity;
    }

}
