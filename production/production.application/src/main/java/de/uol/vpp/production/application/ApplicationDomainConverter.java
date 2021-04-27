package de.uol.vpp.production.application;

import de.uol.vpp.production.application.dto.ProductionDTO;
import de.uol.vpp.production.application.dto.ProductionProducerDTO;
import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ApplicationDomainConverter {

    public ProductionDTO toApplication(ProductionAggregate domainEntity) {
        ProductionDTO dto = new ProductionDTO();
        dto.setActionRequestId(domainEntity.getProductionActionRequestId().getValue());
        dto.setVirtualPowerPlantId(domainEntity.getProductionVirtualPowerPlantId().getValue());
        dto.setStartTimestamp(domainEntity.getProductionStartTimestamp().getTimestamp().toEpochSecond());
        dto.setProducers(domainEntity.getProductionProducers().stream().map(this::toApplication).collect(Collectors.toList()));
        return dto;
    }

    public ProductionProducerDTO toApplication(ProductionProducerEntity domainEntity) {
        ProductionProducerDTO dto = new ProductionProducerDTO();
        dto.setProducerId(domainEntity.getProducerId().getValue());
        dto.setProducerType(domainEntity.getProductionType().getValue());
        dto.setStartTimestamp(domainEntity.getStartTimestamp().getTimestamp().toEpochSecond());
        dto.setCurrentValue(domainEntity.getCurrentValue().getValue());
        dto.setPossibleValue(domainEntity.getPossibleValue().getValue());
        return dto;
    }

}
