package de.uol.vpp.action.domain.valueobjects;

import de.uol.vpp.action.domain.enums.ProblemTypeEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import lombok.Getter;

@Getter
public class ActionCatalogProblemTypeVO {
    private ProblemTypeEnum value;

    public ActionCatalogProblemTypeVO(ProblemTypeEnum value) throws ActionException {
        if (value == null) {
            throw new ActionException("problemType", "ActionCatalog");
        }
        this.value = value;
    }
}
