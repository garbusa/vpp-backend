package de.uol.vpp.action.infrastructure.algorithm;

import de.uol.vpp.action.domain.enums.ActionTypeEnum;
import de.uol.vpp.action.domain.enums.ProblemTypeEnum;
import lombok.Data;

import java.util.TreeSet;

/**
 * Zwischenklasse für die Erstellung von Handlungsempfehlungskataloge
 */
@Data
class DifferenceTimeseries {
    private String actionRequestId;
    private String virtualPowerPlantId;
    private TreeSet<Long> timestamps;
    private ProblemTypeEnum problemType;
    private TreeSet<ActionTypeEnum> actiontype;
    private Boolean maxima;
    private Double averageGap;
}
