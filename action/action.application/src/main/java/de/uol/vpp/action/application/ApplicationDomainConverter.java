package de.uol.vpp.action.application;

import de.uol.vpp.action.application.dto.*;
import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.*;
import de.uol.vpp.action.domain.enums.ManipulationTypeEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.valueobjects.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Konvertierungsklasse für die Konvertierung zwischen Applikations- und Domänenschicht
 */
@Component
public class ApplicationDomainConverter {

    public ActionRequestDTO toApplication(ActionRequestAggregate domainEntity) {
        ActionRequestDTO dto = new ActionRequestDTO();
        dto.setActionRequestId(domainEntity.getActionRequestId().getValue());
        dto.setVirtualPowerPlantId(domainEntity.getVirtualPowerPlantId().getValue());
        dto.setTimestamp(domainEntity.getTimestamp().getValue().toEpochSecond());
        dto.setStatus(domainEntity.getStatus().getValue().toString());
        dto.setOverflowThreshold(domainEntity.getOverflowThreshold().getValue());
        dto.setShortageThreshold(domainEntity.getShortageThreshold().getValue());
        dto.setCatalogs(new ArrayList<>());
        if (domainEntity.getCatalogs() != null && domainEntity.getCatalogs().size() > 0) {
            for (ActionCatalogEntity entity : domainEntity.getCatalogs()) {
                dto.getCatalogs().add(this.toApplication(entity));
            }
        }
        dto.setProducerManipulations(new ArrayList<>());
        if (domainEntity.getProducerManipulations() != null && domainEntity.getProducerManipulations().size() > 0) {
            for (ProducerManipulationEntity entity : domainEntity.getProducerManipulations()) {
                dto.getProducerManipulations().add(this.toApplication(entity));
            }
        }
        dto.setStorageManipulations(new ArrayList<>());
        if (domainEntity.getStorageManipulations() != null && domainEntity.getStorageManipulations().size() > 0) {
            for (StorageManipulationEntity entity : domainEntity.getStorageManipulations()) {
                dto.getStorageManipulations().add(this.toApplication(entity));
            }
        }
        dto.setGridManipulations(new ArrayList<>());
        if (domainEntity.getGridManipulations() != null && domainEntity.getGridManipulations().size() > 0) {
            for (GridManipulationEntity entity : domainEntity.getGridManipulations()) {
                dto.getGridManipulations().add(this.toApplication(entity));
            }
        }
        return dto;
    }

    private ActionCatalogDTO toApplication(ActionCatalogEntity domainEntity) {
        ActionCatalogDTO dto = new ActionCatalogDTO();
        dto.setStartTimestamp(domainEntity.getStartTimestamp().getValue().toEpochSecond());
        dto.setEndTimestamp(domainEntity.getEndTimestamp().getValue().toEpochSecond());
        dto.setProblemType(domainEntity.getProblemType().getValue().toString());
        dto.setAverageGap(domainEntity.getAverageGap().getValue());
        dto.setActions(new ArrayList<>());
        if (domainEntity.getActions() != null && domainEntity.getActions().size() > 0) {
            for (ActionEntity entity : domainEntity.getActions()) {
                dto.getActions().add(this.toApplication(entity));
            }
        }
        return dto;
    }

    private ProducerManipulationDTO toApplication(ProducerManipulationEntity domainEntity) {
        ProducerManipulationDTO dto = new ProducerManipulationDTO();
        dto.setStartTimestamp(domainEntity.getStartEndTimestamp().getStart().toEpochSecond());
        dto.setEndTimestamp(domainEntity.getStartEndTimestamp().getEnd().toEpochSecond());
        dto.setProducerId(domainEntity.getProducerId().getValue());
        dto.setType(domainEntity.getType().getValue().toString());
        dto.setCapacity(domainEntity.getCapacity().getValue());
        return dto;
    }

    private StorageManipulationDTO toApplication(StorageManipulationEntity domainEntity) {
        StorageManipulationDTO dto = new StorageManipulationDTO();
        dto.setStartTimestamp(domainEntity.getStartEndTimestamp().getStart().toEpochSecond());
        dto.setEndTimestamp(domainEntity.getStartEndTimestamp().getEnd().toEpochSecond());
        dto.setStorageId(domainEntity.getStorageId().getValue());
        dto.setType(domainEntity.getType().getValue().toString());
        dto.setRatedPower(0.);
        if (domainEntity.getRatedPower().getValue() > 0.) {
            dto.setRatedPower(domainEntity.getRatedPower().getValue());
        }
        dto.setHours(0.);
        if (domainEntity.getHours().getValue() != null && domainEntity.getHours().getValue() > 0.) {
            dto.setHours(domainEntity.getHours().getValue());
        }
        return dto;
    }

    private GridManipulationDTO toApplication(GridManipulationEntity domainEntity) {
        GridManipulationDTO dto = new GridManipulationDTO();
        dto.setStartTimestamp(domainEntity.getStartEndTimestamp().getStart().toEpochSecond());
        dto.setEndTimestamp(domainEntity.getStartEndTimestamp().getEnd().toEpochSecond());
        dto.setType(domainEntity.getType().getValue().toString());
        dto.setRatedPower(domainEntity.getRatedPower().getValue());
        return dto;
    }

    private ActionDTO toApplication(ActionEntity domainEntity) {
        ActionDTO dto = new ActionDTO();
        dto.setActionType(domainEntity.getActionType().getValue().toString());
        dto.setProducerOrStorageId(domainEntity.getProducerOrStorageId().getValue());
        dto.setHours(domainEntity.getHours().getValue());
        dto.setIsStorage(domainEntity.getIsStorage().getValue());
        dto.setActionValue(domainEntity.getActionValue().getValue());
        return dto;
    }

    public ActionRequestAggregate toDomain(ActionRequestDTO dto) throws ActionException, ManipulationException {
        ActionRequestAggregate domainEntity = new ActionRequestAggregate();
        domainEntity.setActionRequestId(new ActionRequestIdVO(dto.getActionRequestId()));
        domainEntity.setVirtualPowerPlantId(new ActionRequestVirtualPowerPlantIdVO(dto.getVirtualPowerPlantId()));
        domainEntity.setTimestamp(new ActionRequestTimestampVO(dto.getTimestamp()));
        domainEntity.setOverflowThreshold(new ActionRequestOverflowThresholdVO(dto.getOverflowThreshold()));
        domainEntity.setShortageThreshold(new ActionRequestShortageThresholdVO(dto.getShortageThreshold()));
        //domainEntity.setStatus(new ActionRequestStatusVO(dto.getStatus()));
        domainEntity.setCatalogs(new ArrayList<>());

        domainEntity.setProducerManipulations(new ArrayList<>());
        if (dto.getProducerManipulations() != null && dto.getProducerManipulations().size() > 0) {
            for (ProducerManipulationDTO producerManipulationDTO : dto.getProducerManipulations()) {
                domainEntity.getProducerManipulations().add(this.toDomain(producerManipulationDTO, dto.getActionRequestId()));
            }
        }

        domainEntity.setStorageManipulations(new ArrayList<>());
        if (dto.getStorageManipulations() != null && dto.getStorageManipulations().size() > 0) {
            for (StorageManipulationDTO storageManipulationDTO : dto.getStorageManipulations()) {
                domainEntity.getStorageManipulations().add(this.toDomain(storageManipulationDTO, dto.getActionRequestId()));
            }
        }

        domainEntity.setGridManipulations(new ArrayList<>());
        if (dto.getGridManipulations() != null && dto.getGridManipulations().size() > 0) {
            for (GridManipulationDTO gridManipulationDTO : dto.getGridManipulations()) {
                domainEntity.getGridManipulations().add(this.toDomain(gridManipulationDTO, dto.getActionRequestId()));
            }
        }

        return domainEntity;
    }

    private ProducerManipulationEntity toDomain(ProducerManipulationDTO dto, String actionRequestId) throws ManipulationException, ActionException {
        ProducerManipulationEntity domainEntity = new ProducerManipulationEntity();
        this.setManipulationProperties(domainEntity, actionRequestId, dto.getStartTimestamp(), dto.getEndTimestamp(), dto.getType());
        domainEntity.setProducerId(new ProducerManipulationProducerIdVO(dto.getProducerId()));
        domainEntity.setCapacity(new ProducerManipulationCapacityVO(dto.getCapacity()));
        return domainEntity;
    }

    @SuppressWarnings("Duplicates")
    private StorageManipulationEntity toDomain(StorageManipulationDTO dto, String actionRequestId) throws ManipulationException, ActionException {
        StorageManipulationEntity domainEntity = new StorageManipulationEntity();
        this.setManipulationProperties(domainEntity, actionRequestId, dto.getStartTimestamp(), dto.getEndTimestamp(), dto.getType());
        domainEntity.setStorageId(new StorageManipulationStorageIdVO(dto.getStorageId()));
        domainEntity.setHours(new StorageManipulationHoursVO(0.));
        if (dto.getHours() != null && dto.getHours() > 0.) {
            domainEntity.setHours(new StorageManipulationHoursVO(dto.getHours()));
        }
        domainEntity.setRatedPower(new StorageManipulationRatedPowerVO(0.));
        if (dto.getRatedPower() != null && dto.getRatedPower() > 0.) {
            domainEntity.setRatedPower(new StorageManipulationRatedPowerVO(dto.getRatedPower()));
        }
        return domainEntity;
    }

    private GridManipulationEntity toDomain(GridManipulationDTO dto, String actionRequestId) throws ManipulationException, ActionException {
        GridManipulationEntity domainEntity = new GridManipulationEntity();
        this.setManipulationProperties(domainEntity, actionRequestId, dto.getStartTimestamp(), dto.getEndTimestamp(), dto.getType());
        domainEntity.setRatedPower(new GridManipulationRatedPowerVO(dto.getRatedPower()));
        return domainEntity;
    }

    private void setManipulationProperties(AbstractManipulationEntity domainEntity, String actionRequestId, Long start, Long end, String type) throws ManipulationException, ActionException {
        domainEntity.setActionRequestId(new ActionRequestIdVO(actionRequestId));
        domainEntity.setStartEndTimestamp(new ManipulationStartEndTimestampVO(start, end));
        domainEntity.setType(new ManipulationTypeVO(ManipulationTypeEnum.valueOf(type)));
    }


}
