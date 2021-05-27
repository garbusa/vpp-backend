package de.uol.vpp.action.infrastructure.repositories;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;
import de.uol.vpp.action.infrastructure.InfrastructureDomainConverter;
import de.uol.vpp.action.infrastructure.entities.ActionRequest;
import de.uol.vpp.action.infrastructure.jpaRepositories.ActionRequestJpaRepository;
import de.uol.vpp.action.infrastructure.rabbitmq.RabbitMQSender;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.ActionRequestMessage;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.GridManipulationMessage;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.ProducerManipulationMessage;
import de.uol.vpp.action.infrastructure.rabbitmq.messages.StorageManipulationMessage;
import de.uol.vpp.action.infrastructure.rest.LoadRestClient;
import de.uol.vpp.action.infrastructure.rest.ProductionRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementiertes Repository der Schnittstellendefinition aus der Domänenschicht.
 * Ist für die Kommunikation mit dem JPA-Repository zuständig.
 */
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
        } catch (ActionException | ManipulationException e) {
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
        } catch (ActionException | ManipulationException e) {
            throw new ActionRepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public void saveActionRequest(ActionRequestAggregate actionRequest, boolean isInitialSave) throws ActionRepositoryException {
        ActionRequest jpaEntity = converter.toInfrastructure(actionRequest);
        ActionRequest saved = actionRequestJpaRepository.save(jpaEntity);
        if (isInitialSave) {
            if (loadRestClient.isHealthy() && productionRestClient.isHealthy()) {
                ActionRequestMessage actionRequestMessage = this.toMessage(saved);
                sender.sendActionRequest(actionRequestMessage);
            } else {
                saved.setStatus(StatusEnum.FAILED);
                actionRequestJpaRepository.save(saved);
                throw new ActionRepositoryException("Eine Maßnahmanabfrage kann nur erstellt werden, wenn der Erzeugungs- und Lastenservice aktiv sind");
            }
        }
    }

    /**
     * Konvertiert eine Datenbank-Entität in eine Message-Entität für die RabbitMQ
     *
     * @param saved gespeicherte Datenbank-Entität
     * @return Message für die Queue
     */
    private ActionRequestMessage toMessage(ActionRequest saved) {
        ActionRequestMessage actionRequestMessage = new ActionRequestMessage();
        actionRequestMessage.setActionRequestId(saved.getActionRequestId());
        actionRequestMessage.setVppId(saved.getVirtualPowerPlantId());
        actionRequestMessage.setOverflowThreshold(saved.getOverflowThreshold());
        actionRequestMessage.setShortageThreshold(saved.getShortageThreshold());
        actionRequestMessage.setProducerManipulations(saved.getProducerManipulations().stream().map(
                (manipulation) -> {
                    ProducerManipulationMessage manipulationMessage = new ProducerManipulationMessage();
                    manipulationMessage.setStartTimestamp(manipulation.getProducerManipulationPrimaryKey().getManipulationPrimaryKey().getStartTimestamp().toEpochSecond());
                    manipulationMessage.setEndTimestamp(manipulation.getProducerManipulationPrimaryKey().getManipulationPrimaryKey().getEndTimestamp().toEpochSecond());
                    manipulationMessage.setProducerId(manipulation.getProducerManipulationPrimaryKey().getProducerId());
                    manipulationMessage.setType(manipulation.getManipulationType().toString());
                    manipulationMessage.setCapacity(manipulation.getCapacity());
                    return manipulationMessage;
                }
        ).collect(Collectors.toList()));
        actionRequestMessage.setStorageManipulations(saved.getStorageManipulations().stream().map(
                (manipulation) -> {
                    StorageManipulationMessage manipulationMessage = new StorageManipulationMessage();
                    manipulationMessage.setStartTimestamp(manipulation.getStorageManipulationPrimaryKey().getManipulationPrimaryKey().getStartTimestamp().toEpochSecond());
                    manipulationMessage.setEndTimestamp(manipulation.getStorageManipulationPrimaryKey().getManipulationPrimaryKey().getEndTimestamp().toEpochSecond());
                    manipulationMessage.setStorageId(manipulation.getStorageManipulationPrimaryKey().getStorageId());
                    manipulationMessage.setType(manipulation.getManipulationType().toString());
                    manipulationMessage.setHours(manipulation.getHours());
                    manipulationMessage.setRatedPower(manipulation.getRatedPower());
                    return manipulationMessage;
                }
        ).collect(Collectors.toList()));
        actionRequestMessage.setGridManipulations(saved.getGridManipulations().stream().map(
                (manipulation) -> {
                    GridManipulationMessage manipulationMessage = new GridManipulationMessage();
                    manipulationMessage.setStartTimestamp(manipulation.getManipulationPrimaryKey().getStartTimestamp().toEpochSecond());
                    manipulationMessage.setEndTimestamp(manipulation.getManipulationPrimaryKey().getEndTimestamp().toEpochSecond());
                    manipulationMessage.setType(manipulation.getManipulationType().toString());
                    manipulationMessage.setRatedCapacity(manipulation.getRatedPower());
                    return manipulationMessage;
                }
        ).collect(Collectors.toList()));
        return actionRequestMessage;
    }

}
