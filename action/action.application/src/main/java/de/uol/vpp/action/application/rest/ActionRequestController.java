package de.uol.vpp.action.application.rest;

import de.uol.vpp.action.application.ApplicationDomainConverter;
import de.uol.vpp.action.application.dto.ActionRequestDTO;
import de.uol.vpp.action.application.payload.ApiResponse;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionServiceException;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.services.IActionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST-Schnittstelle für die Erstellung und Abfrage von Maßnahmenabfragen
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/action", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionRequestController {

    private final IActionRequestService actionRequestService;
    private final ApplicationDomainConverter converter;

    /**
     * Hole alle Maßnahmenabfragen durch Id des VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Maßnahmenabfragen
     */
    @GetMapping(path = "/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> getAllActionRequestsByVppId(@PathVariable String virtualPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Die Abfrage aller Maßnahmenanfragen war erfolgreich.",
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
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Hole einzelne Maßnahmenabfrage durch Id
     *
     * @param actionRequestId Id der Maßnahmenabfrage
     * @return Maßnahmenabfrage
     */
    @GetMapping(path = "/{actionRequestId}")
    public ResponseEntity<?> getActionRequestById(@PathVariable String actionRequestId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Die Abfrage einer Maßnahmenanfrage war erfolgreich.",
                            converter.toApplication(actionRequestService.get(actionRequestId))), HttpStatus.OK);
        } catch (ActionServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Startet die Erzeugung und den Prozess der Maßnahmenabfrage
     *
     * @param dto Datentransferobjekt der Maßnahmenabfrage
     * @return Austauschobjekt
     */
    @PostMapping()
    public ResponseEntity<?> scheduleActionRequest(@RequestBody ActionRequestDTO dto) {
        try {
            actionRequestService.save(converter.toDomain(dto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "" +
                    "Maßnahmenanfrage wurde erfolgreich angelegt", null));
        } catch (ActionServiceException | ActionException | ManipulationException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }
}
