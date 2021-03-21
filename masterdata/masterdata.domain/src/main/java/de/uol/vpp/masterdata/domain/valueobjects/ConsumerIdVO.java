package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.architecture.ValueObject;
import de.uol.vpp.masterdata.domain.exceptions.ConsumerException;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@Getter
@Setter
public class ConsumerIdVO {

    private final String id;

    public ConsumerIdVO(String id) throws ConsumerException {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new ConsumerException("validation for consumer id failed");
        }
        this.id = id.toUpperCase();
    }
}
