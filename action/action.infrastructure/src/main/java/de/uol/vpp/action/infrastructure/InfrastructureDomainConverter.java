package de.uol.vpp.action.infrastructure;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.entities.ActionEntity;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.valueobjects.*;
import de.uol.vpp.action.infrastructure.entities.Action;
import de.uol.vpp.action.infrastructure.entities.ActionCatalog;
import de.uol.vpp.action.infrastructure.entities.ActionRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Component
public class InfrastructureDomainConverter {

    public ActionRequestAggregate toDomain(ActionRequest jpaEntity) throws ActionException {
        ActionRequestAggregate domainEntity = new ActionRequestAggregate();
        domainEntity.setActionRequestId(new ActionRequestIdVO(jpaEntity.getActionRequestId()));
        domainEntity.setVirtualPowerPlantId(new ActionRequestVirtualPowerPlantIdVO(jpaEntity.getVirtualPowerPlantId()));
        domainEntity.setTimestamp(new ActionRequestTimestampVO(jpaEntity.getTimestamp().toEpochSecond()));
        domainEntity.setStatus(new ActionRequestStatusVO(jpaEntity.getStatus()));
        domainEntity.setCatalogs(new ArrayList<>());
        if (jpaEntity.getCatalogs() != null && jpaEntity.getCatalogs().size() > 0) {
            for (ActionCatalog actionCatalog : jpaEntity.getCatalogs()) {
                domainEntity.getCatalogs().add(this.toDomain(actionCatalog));
            }
        } else {
            domainEntity.setCatalogs(new ArrayList<>());
        }
        return domainEntity;
    }

    public ActionCatalogEntity toDomain(ActionCatalog jpaEntity) throws ActionException {
        ActionCatalogEntity domainEntity = new ActionCatalogEntity();
        domainEntity.setActionRequestIdVO(new ActionRequestIdVO(jpaEntity.getActionCatalogPrimaryKey().getActionRequest().getActionRequestId()));
        domainEntity.setStartTimestamp(new ActionCatalogStartTimestampVO(jpaEntity.getActionCatalogPrimaryKey().getStartTimestamp().toEpochSecond()));
        domainEntity.setEndTimestamp(new ActionCatalogEndTimestampVO(jpaEntity.getActionCatalogPrimaryKey().getEndTimestamp().toEpochSecond()));
        domainEntity.setProblemType(new ActionCatalogProblemTypeVO(jpaEntity.getProblemType()));
        domainEntity.setCumulativeGap(new ActionCatalogCumulativeGapVO(jpaEntity.getCumulativeGap()));
        domainEntity.setActions(new ArrayList<>());
        if (jpaEntity.getActions() != null && jpaEntity.getActions().size() > 0) {
            for (Action action : jpaEntity.getActions()) {
                domainEntity.getActions().add(this.toDomain(action));
            }
        }
        return domainEntity;
    }

    public ActionEntity toDomain(Action jpaEntity) throws ActionException {
        ActionEntity domainEntity = new ActionEntity();
        domainEntity.setActionRequestId(new ActionRequestIdVO(
                jpaEntity.getActionPrimaryKey().getActionCatalog().getActionCatalogPrimaryKey().getActionRequest().getActionRequestId())
        );
        domainEntity.setStartTimestamp(new ActionCatalogStartTimestampVO(
                jpaEntity.getActionPrimaryKey().getActionCatalog().getActionCatalogPrimaryKey().getStartTimestamp().toEpochSecond()
        ));
        domainEntity.setEndTimestamp(new ActionCatalogEndTimestampVO(
                jpaEntity.getActionPrimaryKey().getActionCatalog().getActionCatalogPrimaryKey().getEndTimestamp().toEpochSecond()
        ));
        domainEntity.setActionType(new ActionTypeVO(jpaEntity.getActionPrimaryKey().getActionType()));
        domainEntity.setProducerOrStorageId(new ActionProducerOrStorageIdVO(jpaEntity.getActionPrimaryKey().getProducerOrStorageId()));
        domainEntity.setIsStorage(new ActionIsStorageVO(jpaEntity.getIsStorage()));
        domainEntity.setHours(new ActionHoursVO(jpaEntity.getHours()));
        domainEntity.setActionValue(new ActionValueVO(jpaEntity.getValue()));
        return domainEntity;
    }

    public ActionRequest toInfrastructure(ActionRequestAggregate domainEntity) {
        ActionRequest jpaEntity = new ActionRequest();
        jpaEntity.setActionRequestId(domainEntity.getActionRequestId().getValue());
        jpaEntity.setVirtualPowerPlantId(domainEntity.getVirtualPowerPlantId().getValue());
        jpaEntity.setTimestamp(domainEntity.getTimestamp().getValue());
        jpaEntity.setStatus(domainEntity.getStatus().getValue());
        jpaEntity.setCatalogs(new HashSet<>());
        if (domainEntity.getCatalogs() != null && domainEntity.getCatalogs().size() > 0) {
            for (ActionCatalogEntity actionCatalog : domainEntity.getCatalogs()) {
                jpaEntity.getCatalogs().add(this.toInfrastructure(actionCatalog, jpaEntity));
            }
        }

        return jpaEntity;
    }

    public ActionCatalog toInfrastructure(ActionCatalogEntity domainEntity, ActionRequest actionRequestJpa) {
        ActionCatalog jpaEntity = new ActionCatalog();
        jpaEntity.setActionCatalogPrimaryKey(
                new ActionCatalog.ActionCatalogPrimaryKey(actionRequestJpa,
                        domainEntity.getStartTimestamp().getValue(),
                        domainEntity.getEndTimestamp().getValue())
        );
        jpaEntity.setProblemType(domainEntity.getProblemType().getValue());
        jpaEntity.setCumulativeGap(domainEntity.getCumulativeGap().getValue());
        jpaEntity.setActions(new HashSet<>());
        if (domainEntity.getActions() != null && domainEntity.getActions().size() > 0) {
            for (ActionEntity action : domainEntity.getActions()) {
                jpaEntity.getActions().add(this.toInfrastructure(action, jpaEntity));
            }
        }

        return jpaEntity;
    }

    private Action toInfrastructure(ActionEntity domainEntity, ActionCatalog actionCatalogJpa) {
        Action jpaEntity = new Action();
        jpaEntity.setActionPrimaryKey(
                new Action.ActionPrimaryKey(actionCatalogJpa,
                        domainEntity.getActionType().getValue(),
                        domainEntity.getProducerOrStorageId().getValue())
        );
        jpaEntity.setHours(domainEntity.getHours().getValue());
        jpaEntity.setIsStorage(domainEntity.getIsStorage().getValue());
        jpaEntity.setValue(domainEntity.getActionValue().getValue());
        return jpaEntity;
    }

}
