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

    @GetMapping(path = "/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> getAllActionRequestsByVppId(@PathVariable String virtualPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Maßnahmenanfragen war erfolgreich",
                            actionRequestService.getAllActionRequestByVppId(virtualPowerPlantId)
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
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{actionRequestId}")
    public ResponseEntity<?> getActionRequestById(@PathVariable String actionRequestId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage einer Maßnahmenanfrage war erfolgreich",
                            converter.toApplication(actionRequestService.get(actionRequestId))), HttpStatus.OK);
        } catch (ActionServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/schedule/{virtualPowerPlantId}")
    public ResponseEntity<?> scheduleActionRequest(@PathVariable String virtualPowerPlantId) {
        try {
            ActionRequestDTO dto = new ActionRequestDTO();
            dto.setActionRequestId(SecureRandomString.generate());
            dto.setTimestamp(Instant.now().getEpochSecond());
            dto.setVirtualPowerPlantId(virtualPowerPlantId);
            actionRequestService.save(converter.toDomain(dto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "" +
                    "Maßnahmenanfrage wurde erfolgreich angelegt", null));
        } catch (ActionServiceException | ActionException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }
}
