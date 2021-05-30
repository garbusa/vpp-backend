package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.DecentralizedPowerPlantDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantServiceException;
import de.uol.vpp.masterdata.domain.services.IDecentralizedPowerPlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST-Ressource für dezentrale Kraftwerke
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/dpp", produces = MediaType.APPLICATION_JSON_VALUE)
public class DecentralizedPowerPlantController {

    private final IDecentralizedPowerPlantService service;
    private final ApplicationDomainConverter converter;

    /**
     * Holt alle DKs aus einem VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste aller DK
     */
    @GetMapping(path = "/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> getAllDecentralizedPowerPlantsByVirtualPowerPlantId(@PathVariable String virtualPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Alle DK wurden erfolgreich abgefragt.",
                            service.getAllByVppId(virtualPowerPlantId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (DecentralizedPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Holt ein spezifisches DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return DK
     */
    @GetMapping(path = "/{decentralizedPowerPlantId}")
    public ResponseEntity<?> getOneDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Alle DK wurden erfolgreich abgefragt.",
                            converter.toApplication(service.get(decentralizedPowerPlantId)))
                    , HttpStatus.OK);
        } catch (DecentralizedPowerPlantServiceException e) {
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
     * Persisierung eines DK
     *
     * @param dto                 zu speichernde DK
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @PostMapping(path = "/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> saveDecentralizedPowerPlant(@RequestBody DecentralizedPowerPlantDTO dto,
                                                         @PathVariable String virtualPowerPlantId) {
        try {
            service.save(converter.toDomain(dto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, String.format("Das DK %s wurde erfolgreich angelegt", dto.getDecentralizedPowerPlantId()), null));
        } catch (DecentralizedPowerPlantServiceException | DecentralizedPowerPlantException e) {
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
     * Löscht ein DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @param virtualPowerPlantId       Id des VK
     * @return ApiResponse ohne Daten
     */
    @DeleteMapping(path = "/{decentralizedPowerPlantId}")
    public ResponseEntity<?> deleteDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId, @RequestParam String virtualPowerPlantId) {
        try {
            service.delete(decentralizedPowerPlantId, virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, String.format("Das DK %s wurde erfolgreich gelöscht.", decentralizedPowerPlantId), null));
        } catch (DecentralizedPowerPlantServiceException e) {
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
     * Aktualisiert ein existierendes DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @param newDto                    aktualisierte Daten
     * @param virtualPowerPlantId       Id des VK
     * @return ApiResponse ohne Daten
     */
    @PutMapping(path = "/{decentralizedPowerPlantId}")
    public ResponseEntity<?> updateDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId, @RequestBody DecentralizedPowerPlantDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(decentralizedPowerPlantId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, String.format("Das DK %s wurde erfolgreich aktualisiert.", decentralizedPowerPlantId), null));
        } catch (DecentralizedPowerPlantServiceException | DecentralizedPowerPlantException e) {
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
