package de.uol.vpp.action.infrastructure;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.*;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.valueobjects.*;
import de.uol.vpp.action.infrastructure.entities.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Component
public class InfrastructureDomainConverter {

    public ActionRequestAggregate toDomain(ActionRequest jpaEntity) throws ActionException, ManipulationException {
        ActionRequestAggregate domainEntity = new ActionRequestAggregate();
        domainEntity.setActionRequestId(new ActionRequestIdVO(jpaEntity.getActionRequestId()));
        domainEntity.setVirtualPowerPlantId(new ActionRequestVirtualPowerPlantIdVO(jpaEntity.getVirtualPowerPlantId()));
        domainEntity.setTimestamp(new ActionRequestTimestampVO(jpaEntity.getTimestamp().toEpochSecond()));
        domainEntity.setStatus(new ActionRequestStatusVO(jpaEntity.getStatus()));
        domainEntity.setShortageThreshold(new ActionRequestShortageThresholdVO(jpaEntity.getShortageThreshold()));
        domainEntity.setOverflowThreshold(new ActionRequestOverflowThresholdVO(jpaEntity.getOverflowThreshold()));
        domainEntity.setCatalogs(new ArrayList<>());
        if (jpaEntity.getCatalogs() != null && jpaEntity.getCatalogs().size() > 0) {
            for (ActionCatalog actionCatalog : jpaEntity.getCatalogs()) {
                domainEntity.getCatalogs().add(this.toDomain(actionCatalog));
            }
        }
        domainEntity.setProducerManipulations(new ArrayList<>());
        if (jpaEntity.getProducerManipulations() != null && jpaEntity.getProducerManipulations().size() > 0) {
            for (ProducerManipulation producerManipulation : jpaEntity.getProducerManipulations()) {
                domainEntity.getProducerManipulations().add(this.toDomain(producerManipulation));
            }
        }
        domainEntity.setStorageManipulations(new ArrayList<>());
        if (jpaEntity.getStorageManipulations() != null && jpaEntity.getStorageManipulations().size() > 0) {
            for (StorageManipulation storageManipulation : jpaEntity.getStorageManipulations()) {
                domainEntity.getStorageManipulations().add(this.toDomain(storageManipulation));
            }
        }
        domainEntity.setGridManipulations(new ArrayList<>());
        if (jpaEntity.getGridManipulations() != null && jpaEntity.getGridManipulations().size() > 0) {
            for (GridManipulation gridManipulation : jpaEntity.getGridManipulations()) {
                domainEntity.getGridManipulations().add(this.toDomain(gridManipulation));
            }
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

    private ProducerManipulationEntity toDomain(ProducerManipulation jpaEntity) throws ManipulationException {
        ProducerManipulationEntity domainEntity = new ProducerManipulationEntity();
        domainEntity.setActionRequestId(
                new ProducerManipulationActionRequestIdVO(
                        jpaEntity.getProducerManipulationPrimaryKey().getActionRequest().getActionRequestId()
                )
        );
        domainEntity.setStartEndTimestamp(new ProducerManipulationStartEndTimestampVO(
                jpaEntity.getProducerManipulationPrimaryKey().getStartTimestamp().toEpochSecond(),
                jpaEntity.getProducerManipulationPrimaryKey().getEndTimestamp().toEpochSecond()
        ));
        domainEntity.setProducerId(new ProducerManipulationProducerIdVO(
                jpaEntity.getProducerManipulationPrimaryKey().getProducerId()
        ));
        domainEntity.setType(new ProducerManipulationTypeVO(jpaEntity.getManipulationType()));
        domainEntity.setCapacity(new ProducerManipulationCapacityVO(jpaEntity.getCapacity()));
        return domainEntity;
    }

    private StorageManipulationEntity toDomain(StorageManipulation jpaEntity) throws ManipulationException {
        StorageManipulationEntity domainEntity = new StorageManipulationEntity();
        domainEntity.setActionRequestId(
                new StorageManipulationActionRequestIdVO(
                        jpaEntity.getStorageManipulationPrimaryKey().getActionRequest().getActionRequestId()
                )
        );
        domainEntity.setStartEndTimestamp(new StorageManipulationStartEndTimestampVO(
                jpaEntity.getStorageManipulationPrimaryKey().getStartTimestamp().toEpochSecond(),
                jpaEntity.getStorageManipulationPrimaryKey().getEndTimestamp().toEpochSecond()
        ));
        domainEntity.setStorageId(new StorageManipulationStorageIdVO(
                jpaEntity.getStorageManipulationPrimaryKey().getStorageId()
        ));
        domainEntity.setHours(new StorageManipulationHoursVO(0.));
        if (jpaEntity.getHours() != null && jpaEntity.getHours() > 0.) {
            domainEntity.setHours(new StorageManipulationHoursVO(jpaEntity.getHours()));
        }
        domainEntity.setRatedPower(new StorageManipulationRatedPowerVO(0.));
        if (jpaEntity.getRatedPower() != null && jpaEntity.getRatedPower() > 0.) {
            domainEntity.setRatedPower(new StorageManipulationRatedPowerVO(jpaEntity.getRatedPower()));
        }
        domainEntity.setType(new StorageManipulationTypeVO(jpaEntity.getManipulationType()));
        return domainEntity;
    }

    private GridManipulationEntity toDomain(GridManipulation jpaEntity) throws ManipulationException {
        GridManipulationEntity domainEntity = new GridManipulationEntity();
        domainEntity.setActionRequestId(
                new GridManipulationActionRequestIdVO(
                        jpaEntity.getGridManipulationPrimaryKey().getActionRequest().getActionRequestId()
                )
        );
        domainEntity.setStartEndTimestamp(new GridManipulationStartEndTimestampVO(
                jpaEntity.getGridManipulationPrimaryKey().getStartTimestamp().toEpochSecond(),
                jpaEntity.getGridManipulationPrimaryKey().getEndTimestamp().toEpochSecond()
        ));
        domainEntity.setType(new GridManipulationTypeVO(jpaEntity.getManipulationType()));
        domainEntity.setRatedPower(new GridManipulationRatedPowerVO(jpaEntity.getRatedPower()));
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
        jpaEntity.setShortageThreshold(domainEntity.getShortageThreshold().getValue());
        jpaEntity.setOverflowThreshold(domainEntity.getOverflowThreshold().getValue());
        jpaEntity.setCatalogs(new HashSet<>());
        if (domainEntity.getCatalogs() != null && domainEntity.getCatalogs().size() > 0) {
            for (ActionCatalogEntity actionCatalog : domainEntity.getCatalogs()) {
                jpaEntity.getCatalogs().add(this.toInfrastructure(actionCatalog, jpaEntity));
            }
        }
        jpaEntity.setProducerManipulations(new HashSet<>());
        if (domainEntity.getProducerManipulations() != null && domainEntity.getProducerManipulations().size() > 0) {
            for (ProducerManipulationEntity producerManipulation : domainEntity.getProducerManipulations()) {
                jpaEntity.getProducerManipulations().add(this.toInfrastructure(producerManipulation, jpaEntity));
            }
        }
        jpaEntity.setStorageManipulations(new HashSet<>());
        if (domainEntity.getStorageManipulations() != null && domainEntity.getStorageManipulations().size() > 0) {
            for (StorageManipulationEntity storageManipulation : domainEntity.getStorageManipulations()) {
                jpaEntity.getStorageManipulations().add(this.toInfrastructure(storageManipulation, jpaEntity));
            }
        }
        jpaEntity.setGridManipulations(new HashSet<>());
        if (domainEntity.getGridManipulations() != null && domainEntity.getGridManipulations().size() > 0) {
            for (GridManipulationEntity gridManipulation : domainEntity.getGridManipulations()) {
                jpaEntity.getGridManipulations().add(this.toInfrastructure(gridManipulation, jpaEntity));
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

    private ProducerManipulation toInfrastructure(ProducerManipulationEntity domainEntity, ActionRequest actionRequest) {
        ProducerManipulation jpaEntity = new ProducerManipulation();
        jpaEntity.setProducerManipulationPrimaryKey(
                new ProducerManipulation.ProducerManipulationPrimaryKey(
                        actionRequest,
                        domainEntity.getStartEndTimestamp().getStart(),
                        domainEntity.getStartEndTimestamp().getEnd(),
                        domainEntity.getProducerId().getValue()
                )
        );
        jpaEntity.setManipulationType(domainEntity.getType().getValue());
        jpaEntity.setCapacity(domainEntity.getCapacity().getValue());
        return jpaEntity;
    }

    private StorageManipulation toInfrastructure(StorageManipulationEntity domainEntity, ActionRequest actionRequest) {
        StorageManipulation jpaEntity = new StorageManipulation();
        jpaEntity.setStorageManipulationPrimaryKey(
                new StorageManipulation.StorageManipulationPrimaryKey(
                        actionRequest,
                        domainEntity.getStartEndTimestamp().getStart(),
                        domainEntity.getStartEndTimestamp().getEnd(),
                        domainEntity.getStorageId().getValue()
                )
        );
        jpaEntity.setManipulationType(domainEntity.getType().getValue());
        jpaEntity.setHours(0.);
        if (domainEntity.getHours().getValue() != null && domainEntity.getHours().getValue() > 0.) {
            jpaEntity.setHours(domainEntity.getHours().getValue());
        }
        jpaEntity.setRatedPower(0.);
        if (domainEntity.getRatedPower().getValue() > 0.) {
            jpaEntity.setRatedPower(domainEntity.getRatedPower().getValue());
        }
        return jpaEntity;
    }

    private GridManipulation toInfrastructure(GridManipulationEntity domainEntity, ActionRequest actionRequest) {
        GridManipulation jpaEntity = new GridManipulation();
        jpaEntity.setGridManipulationPrimaryKey(
                new GridManipulation.GridManipulationPrimaryKey(
                        actionRequest,
                        domainEntity.getStartEndTimestamp().getStart(),
                        domainEntity.getStartEndTimestamp().getEnd()
                )
        );
        jpaEntity.setManipulationType(domainEntity.getType().getValue());
        jpaEntity.setRatedPower(domainEntity.getRatedPower().getValue());
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
