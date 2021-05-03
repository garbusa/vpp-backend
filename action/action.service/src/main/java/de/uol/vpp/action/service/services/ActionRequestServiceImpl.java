package de.uol.vpp.action.service.services;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.exceptions.ActionServiceException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.services.IActionRequestService;
import de.uol.vpp.action.domain.utils.SecureRandomString;
import de.uol.vpp.action.domain.valueobjects.ActionRequestIdVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestStatusVO;
import de.uol.vpp.action.domain.valueobjects.ActionRequestVirtualPowerPlantIdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionRequestServiceImpl implements IActionRequestService {

    private final IActionRequestRepository actionRequestRepository;

    @Override
    public List<ActionRequestAggregate> getAllActionRequestByVppId(String virtualPowerPlantId) throws ActionServiceException {
        try {
            return actionRequestRepository.getAllActionRequestsByVppId(new ActionRequestVirtualPowerPlantIdVO(virtualPowerPlantId));
        } catch (ActionRepositoryException | ActionException e) {
            throw new ActionServiceException(e.getMessage(), e);
        }
    }

    @Override
    public ActionRequestAggregate get(String actionRequestId) throws ActionServiceException {
        try {
            Optional<ActionRequestAggregate> actionRequest = actionRequestRepository.getActionRequest(
                    new ActionRequestIdVO(actionRequestId)
            );
            if (actionRequest.isPresent()) {
                return actionRequest.get();
            } else {
                throw new ActionServiceException(
                        String.format("ActionRequest mit der ID %s konnte nicht gefunden werden", actionRequestId)
                );
            }
        } catch (ActionRepositoryException | ActionException e) {
            throw new ActionServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void save(ActionRequestAggregate domainEntity) throws ActionServiceException {
        try {
            while (actionRequestRepository.getActionRequest(domainEntity.getActionRequestId()).isPresent()) {
                domainEntity.setActionRequestId(new ActionRequestIdVO(SecureRandomString.generate()));
            }
            domainEntity.setStatus(new ActionRequestStatusVO(StatusEnum.STARTED)); //initial started
            actionRequestRepository.saveActionRequest(domainEntity, true);
        } catch (ActionRepositoryException | ActionException e) {
            throw new ActionServiceException(e.getMessage(), e);
        }

    }
}
