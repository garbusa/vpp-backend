package de.uol.vpp.action.infrastructure.repositories;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;
import de.uol.vpp.action.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.action.infrastructure.entities.ActionRequest;
import de.uol.vpp.action.infrastructure.jpaRepositories.ActionRequestJpaRepository;
import de.uol.vpp.action.infrastructure.rabbitmq.RabbitMQSender;
import de.uol.vpp.action.infrastructure.rest.LoadRestClient;
import de.uol.vpp.action.infrastructure.rest.ProductionRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionRequestRepositoryImpl implements IActionRequestRepository {

    private final ActionRequestJpaRepository actionRequestJpaRepository;
    private final InfrastructureDomainConverter converter;
    private final RabbitMQSender sender;
    private final LoadRestClient loadRestClient;
    private final ProductionRestClient productionRestClient;


    @Override
    public List<ActionRequestAggregate> getAllActionRequestsByVppId(ActionRequestVirtualPowerPlantIdVO virtualPowerPlantId) throws ActionRepositoryException {
        try {
            List<ActionRequest> actionRequests = actionRequestJpaRepository.findAllByVirtualPowerPlantId(virtualPowerPlantId.getValue());
            List<ActionRequestAggregate> result = new ArrayList<>();
            for (ActionRequest actionRequest : actionRequests) {
                result.add(converter.toDomain(actionRequest));
            }

            return result;
        } catch (ActionException e) {
            throw new ActionRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<ActionRequestAggregate> getActionRequest(ActionRequestIdVO actionRequestId) throws ActionRepositoryException {
        try {
            Optional<ActionRequest> actionRequest = actionRequestJpaRepository.findById(actionRequestId.getValue());
            if (actionRequest.isPresent()) {
                return Optional.of(converter.toDomain(actionRequest.get()));
            } else {
                return Optional.empty();
            }
        } catch (ActionException e) {
            throw new ActionRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void saveActionRequest(ActionRequestAggregate actionRequest, boolean isInitialSave) throws ActionRepositoryException {
        ActionRequest jpaEntity = converter.toInfrastructure(actionRequest);
        ActionRequest saved = actionRequestJpaRepository.save(jpaEntity);
        if (isInitialSave) {
            if (loadRestClient.isHealthy() && productionRestClient.isHealthy()) {
                sender.sendActionRequest(jpaEntity.getActionRequestId(), jpaEntity.getVirtualPowerPlantId());
            } else {
                saved.setStatus(StatusEnum.FAILED);
                actionRequestJpaRepository.save(saved);
                throw new ActionRepositoryException("Eine Maßnahmanabfrage kann nur erstellt werden, wenn der Erzeugungs- und Lastenservice aktiv sind");
            }
        }
    }

}
