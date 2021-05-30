package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.HouseholdDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdServiceException;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Rest-Ressource für Haushalte
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/household", produces = MediaType.APPLICATION_JSON_VALUE)
public class HouseholdController {

    private final IHouseholdService service;
    private final ApplicationDomainConverter converter;

    /**
     * Holt alle Haushalte eines VK
     *
     * @param virtualPowerPlantId Id des VK
     * @return Liste aller Haushalte
     */
    @GetMapping(path = "/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> getAllHouseholdsByVirtualPowerPlantId(@PathVariable String virtualPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Die Haushalte wurden erfolgreich abgefragt.",
                            service.getAllByVppId(virtualPowerPlantId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Holt ein spezifisches Haushalt
     *
     * @param householdId Id des Haushalts
     * @return Haushalt
     */
    @GetMapping(path = "/{householdId}")
    public ResponseEntity<?> getOneHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Die Haushalte wurden erfolgreich abgefragt.",
                            converter.toApplication(service.get(householdId)))
                    , HttpStatus.OK);
        } catch (HouseholdServiceException e) {
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
     * Persistiert ein Haushalt und weist es einem VK zu
     *
     * @param dto                 zu speicherndes Haushalt
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @PostMapping("/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> saveHousehold(@RequestBody HouseholdDTO dto,
                                           @PathVariable String virtualPowerPlantId) {
        try {
            service.save(converter.toDomain(dto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, String.format("Der Haushalt %s wurde erfolgreich angelegt.", dto.getHouseholdId()), null));
        } catch (HouseholdServiceException | HouseholdException e) {
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
     * Löscht ein Haushalt
     *
     * @param householdId         Id des Haushalts
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @DeleteMapping(path = "/{householdId}")
    public ResponseEntity<?> deleteHousehold(@PathVariable String householdId, String virtualPowerPlantId) {
        try {
            service.delete(householdId, virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, String.format("Der Haushalt %s wurde erfolgreich gelöscht.", householdId), null));
        } catch (HouseholdServiceException e) {
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
     * Aktualisiert ein Haushalt
     *
     * @param householdId         Id des Haushalts
     * @param newDto              aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @PutMapping(path = "/{householdId}")
    public ResponseEntity<?> updateHousehold(@PathVariable String householdId, @RequestBody HouseholdDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(householdId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    String.format("Der Haushalt %s wurde erfolgreich aktualisiert.", householdId), null));
        } catch (HouseholdServiceException | HouseholdException e) {
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
