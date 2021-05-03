package de.uol.vpp.action.application;

import de.uol.vpp.action.application.dto.ActionCatalogDTO;
import de.uol.vpp.action.application.dto.ActionDTO;
import de.uol.vpp.action.application.dto.ActionRequestDTO;
import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.entities.ActionEntity;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestTimestampVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ApplicationDomainConverter {

    public ActionRequestDTO toApplication(ActionRequestAggregate domainEntity) {
        ActionRequestDTO dto = new ActionRequestDTO();
        dto.setActionRequestId(domainEntity.getActionRequestId().getValue());
        dto.setVirtualPowerPlantId(domainEntity.getVirtualPowerPlantId().getValue());
        dto.setTimestamp(domainEntity.getTimestamp().getValue().toEpochSecond());
        dto.setStatus(domainEntity.getStatus().getValue().toString());
        dto.setCatalogs(new ArrayList<>());
        if (domainEntity.getCatalogs() != null && domainEntity.getCatalogs().size() > 0) {
            for (ActionCatalogEntity entity : domainEntity.getCatalogs()) {
                dto.getCatalogs().add(this.toApplication(entity));
            }
        }
        return dto;
    }

    public ActionCatalogDTO toApplication(ActionCatalogEntity domainEntity) {
        ActionCatalogDTO dto = new ActionCatalogDTO();
        dto.setStartTimestamp(domainEntity.getStartTimestamp().getValue().toEpochSecond());
        dto.setEndTimestamp(domainEntity.getEndTimestamp().getValue().toEpochSecond());
        dto.setProblemType(domainEntity.getProblemType().getValue().toString());
        dto.setCumulativeGap(domainEntity.getCumulativeGap().getValue());
        dto.setActions(new ArrayList<>());
        if (domainEntity.getActions() != null && domainEntity.getActions().size() > 0) {
            for (ActionEntity entity : domainEntity.getActions()) {
                dto.getActions().add(this.toApplication(entity));
            }
        }
        return dto;
    }

    public ActionDTO toApplication(ActionEntity domainEntity) {
        ActionDTO dto = new ActionDTO();
        dto.setActionType(domainEntity.getActionType().getValue().toString());
        dto.setProducerOrStorageId(domainEntity.getProducerOrStorageId().getValue());
        dto.setHours(domainEntity.getHours().getValue());
        dto.setIsStorage(domainEntity.getIsStorage().getValue());
        dto.setActionValue(domainEntity.getActionValue().getValue());
        return dto;
    }

    public ActionRequestAggregate toDomain(ActionRequestDTO dto) throws ActionException {
        ActionRequestAggregate domainEntity = new ActionRequestAggregate();
        domainEntity.setActionRequestId(new ActionRequestIdVO(dto.getActionRequestId()));
        domainEntity.setVirtualPowerPlantId(new ActionRequestVirtualPowerPlantIdVO(dto.getVirtualPowerPlantId()));
        domainEntity.setTimestamp(new ActionRequestTimestampVO(dto.getTimestamp()));
        //domainEntity.setStatus(new ActionRequestStatusVO(dto.getStatus()));
        domainEntity.setCatalogs(new ArrayList<>());
        return domainEntity;
    }


}
