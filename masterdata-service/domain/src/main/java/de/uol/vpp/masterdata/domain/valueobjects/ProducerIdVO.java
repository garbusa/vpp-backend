package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class ProducerIdVO {

    private final String id;

    public ProducerIdVO(String id) throws ProducerException {
        if (id == null || id.isEmpty() | id.isBlank()) {
            throw new ProducerException("validation for producer id failed");
        }
        this.id = id.toUpperCase();
    }
}
