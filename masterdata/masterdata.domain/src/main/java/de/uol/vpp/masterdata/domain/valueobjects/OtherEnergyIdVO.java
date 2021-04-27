package de.uol.vpp.masterdata.domain.valueobjects;

import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import lombok.Getter;

@Getter
public class OtherEnergyIdVO {
    private String value;

    public OtherEnergyIdVO(String value) throws ProducerException {
        if (value == null || value.isEmpty() || value.isBlank()) {
            throw new ProducerException("validation for otherEnergy actionRequestId failed");
        }
        this.value = value.toUpperCase();
    }
}
