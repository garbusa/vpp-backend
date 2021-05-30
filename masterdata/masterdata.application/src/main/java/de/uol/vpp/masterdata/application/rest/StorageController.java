package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.StorageDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import de.uol.vpp.masterdata.domain.exceptions.StorageServiceException;
import de.uol.vpp.masterdata.domain.services.IStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Rest-Ressource für Speicheranlage
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/storage", produces = MediaType.APPLICATION_JSON_VALUE)
public class StorageController {

    private final IStorageService service;
    private final ApplicationDomainConverter converter;

    /**
     * Hole alle Speicheranlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste von Speicheranlagen
     */
    @GetMapping(path = "/by/dpp/{" +
            "decentralizedPowerPlantId}")
    public ResponseEntity<?> getAllStoragesByDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Die Speicheranlagen wurden erfolgreich angefragt.",
                            service.getAllByDecentralizedPowerPlantId(decentralizedPowerPlantId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Hole alle Speicheranlagen eines Haushalts
     *
     * @param householdId Id des Haushalts
     * @return Liste von Speicheranlagen
     */
    @GetMapping(path = "/by/household/{" +
            "householdId}")
    public ResponseEntity<?> getAllStoragesByHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Die Speicheranlagen wurden erfolgreich angefragt.",
                            service.getAllByHouseholdId(householdId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler aufgetreten.", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Hole eine spezifische Speicheranlage
     *
     * @param storageId Id der Speicheranlage
     * @return Speicheranlage
     */
    @GetMapping(path = "/{storageId}")
    public ResponseEntity<?> getOneStorage(@PathVariable String storageId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, String.format("Die Speicheranlage %s wurde erfolgreich angefragt.", storageId), converter.toApplication(service.get(storageId)))
                    , HttpStatus.OK);
        } catch (StorageServiceException e) {
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
     * Persistiert Speicheranlage und weist es einem DK zu
     *
     * @param dto                       zu speichernde Daten
     * @param decentralizedPowerPlantId Id des DK
     * @return ApiResponse ohne Daten
     */
    @PostMapping("/by/dpp/{decentralizedPowerPlantId}")
    public ResponseEntity<?> saveStorageWithDecentralizedPowerPlant(@RequestBody StorageDTO dto,
                                                                    @PathVariable String decentralizedPowerPlantId) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, String.format("Die Speicheranlage %s wurde erfolgreich angelegt.", dto.getStorageId()), null));
        } catch (StorageServiceException | StorageException e) {
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
     * Persistiert Speicheranlage und weist es einem Haushalt zu
     *
     * @param dto         zu speichernde Daten
     * @param householdId Id des Haushalts
     * @return ApiResponse ohne Daten
     */
    @PostMapping("/by/household/{householdId}")
    public ResponseEntity<?> saveStorageWithHousehold(@RequestBody StorageDTO dto,
                                                      @PathVariable String householdId) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, String.format("Die Speicheranlage %s wurde erfolgreich angelegt.", dto.getStorageId()), null));
        } catch (StorageServiceException | StorageException e) {
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
     * Löscht eine Speicheranlage
     *
     * @param storageId           Id der Speicheranlage
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @DeleteMapping(path = "/{storageId}")
    public ResponseEntity<?> deleteStorage(@PathVariable String storageId, @RequestParam String virtualPowerPlantId) {
        try {
            service.delete(storageId, virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, String.format("Die Speicheranlage %s wurde erfolgreich gelöscht.", storageId), null));
        } catch (StorageServiceException e) {
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
     * Aktualisiert eine Speicheranlage
     *
     * @param storageId           Id der Speicheranlage
     * @param newDto              aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @PutMapping(path = "/{storageId}")
    public ResponseEntity<?> updateStorage(@PathVariable String storageId, @RequestBody StorageDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(storageId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, String.format("Die Speicheranlage %s wurde erfolgreich aktualisiert.", storageId), null));
        } catch (StorageServiceException | StorageException e) {
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
