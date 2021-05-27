package de.uol.vpp.masterdata.application.dto;

import de.uol.vpp.masterdata.application.dto.abstracts.DtoHasProducersAndStorages;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Datentransferobjekt zw. Benutzeroberfläche und Planungssystem
 * Siehe {@link de.uol.vpp.masterdata.domain.aggregates.HouseholdAggregate}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HouseholdDTO extends DtoHasProducersAndStorages {
    private String householdId;
    private Integer householdMemberAmount;
}
