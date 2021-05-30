package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.ProblemTypeEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

/**
 * Siehe {@link de.uol.vpp.action.domain.entities.ActionCatalogEntity}
 */
@Getter
public class ActionCatalogProblemTypeVO {
    private ProblemTypeEnum value;

    public ActionCatalogProblemTypeVO(ProblemTypeEnum value) throws ActionException {
        if (value == null) {
            throw new ActionException("problemType", "Handlungsempfehlungskatalog");
        }
        this.value = value;
    }
}
