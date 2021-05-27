package de.uol.vpp.production.infrastructure;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.valueobjects.*;
import de.uol.vpp.production.infrastructure.entities.Production;
import de.uol.vpp.production.infrastructure.entities.ProductionProducer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Konvertierungsklasse zwischen der Infrastruktur- und Domänenschicht
 */
@Component
public class InfrastructureDomainConverter {

    public Production toInfrastructure(ProductionAggregate domainEntity) {
        Production jpaEntity = new Production();
        jpaEntity.setActionRequestTimestamp(new Production.ActionRequestTimestamp(
                domainEntity.getProductionActionRequestId().getValue(),
                domainEntity.getProductionStartTimestamp().getTimestamp()
        ));
        jpaEntity.setVirtualPowerPlantId(domainEntity.getProductionVirtualPowerPlantId().getValue());
        if (domainEntity.getProductionProducers() != null && !domainEntity.getProductionProducers().isEmpty()) {
            jpaEntity.setProducers(
                    domainEntity.getProductionProducers().stream().map(this::toInfrastructure)
                            .collect(Collectors.toList())
            );
        } else {
            jpaEntity.setProducers(
                    new ArrayList<>()
            );
        }
        return jpaEntity;
    }

    public ProductionProducer toInfrastructure(ProductionProducerEntity domainEntity) {
        ProductionProducer jpaEntity = new ProductionProducer();
        jpaEntity.setProducerId(domainEntity.getProducerId().getValue());
        jpaEntity.setProducerType(domainEntity.getProductionType().getValue());
        jpaEntity.setCurrentValue(domainEntity.getCurrentValue().getValue());
        jpaEntity.setPossibleValue(domainEntity.getPossibleValue().getValue());
        jpaEntity.setTimestamp(domainEntity.getStartTimestamp().getTimestamp());
        return jpaEntity;
    }

    public ProductionAggregate toDomain(Production jpaEntity) throws ProductionException {
        ProductionAggregate domainEntity = new ProductionAggregate();
        domainEntity.setProductionActionRequestId(new ProductionActionRequestIdVO(jpaEntity.getActionRequestTimestamp().getActionRequestId()));
        domainEntity.setProductionStartTimestamp(new ProductionStartTimestampVO(jpaEntity.getActionRequestTimestamp().getTimestamp().toEpochSecond()));
        domainEntity.setProductionVirtualPowerPlantId(new ProductionVirtualPowerPlantIdVO(jpaEntity.getVirtualPowerPlantId()));
        List<ProductionProducerEntity> producers = new ArrayList<>();
        for (ProductionProducer producerJpa : jpaEntity.getProducers()) {
            producers.add(this.toDomain(producerJpa));
        }
        domainEntity.setProductionProducers(producers);
        return domainEntity;
    }

    private ProductionProducerEntity toDomain(ProductionProducer jpaEntity) throws ProductionException {
        ProductionProducerEntity domainEntity = new ProductionProducerEntity();
        domainEntity.setProducerId(new ProductionProducerIdVO(jpaEntity.getProducerId()));
        domainEntity.setProductionType(new ProductionProducerTypeVO(jpaEntity.getProducerType()));
        domainEntity.setStartTimestamp(new ProductionProducerStartTimestampVO(jpaEntity.getTimestamp().toEpochSecond()));
        domainEntity.setCurrentValue(new ProductionProducerCurrentValueVO(jpaEntity.getCurrentValue()));
        domainEntity.setPossibleValue(new ProductionProducerPossibleValueVO(jpaEntity.getPossibleValue()));
        return domainEntity;
    }

}
