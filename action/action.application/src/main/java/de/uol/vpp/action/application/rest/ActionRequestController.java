package de.uol.vpp.action.application.rest;

import de.uol.vpp.action.application.ApplicationDomainConverter;
import de.uol.vpp.action.application.dto.ActionRequestDTO;
import de.uol.vpp.action.application.payload.ApiResponse;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionServiceException;
import de.uol.vpp.action.domain.services.IActionRequestService;
import de.uol.vpp.action.domain.utils.SecureRandomString;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/action", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionRequestController {

    private final IActionRequestService actionRequestService;
    private final ApplicationDomainConverter converter;

    @GetMapping(path = "/by/vpp/{vppBusinessKey}")
    public ResponseEntity<?> getAllActionRequestsByVppId(@PathVariable String vppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "action requests successfully fetched.",
                            actionRequestService.getAllActionRequestByVppId(vppBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ActionServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{actionRequestId}")
    public ResponseEntity<?> getActionRequestById(@PathVariable String actionRequestId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "action requests successfully fetched.",
                            converter.toApplication(actionRequestService.get(actionRequestId))), HttpStatus.OK);
        } catch (ActionServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/schedule/{vppBusinessKey}")
    public ResponseEntity<?> scheduleActionRequest(@PathVariable String vppBusinessKey) {
        try {
            ActionRequestDTO dto = new ActionRequestDTO();
            dto.setActionRequestId(SecureRandomString.generate());
            dto.setTimestamp(Instant.now().getEpochSecond());
            dto.setVirtualPowerPlantId(vppBusinessKey);
            actionRequestService.save(converter.toDomain(dto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "" +
                    "action request successfully created", null));
        } catch (ActionServiceException | ActionException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }
}
