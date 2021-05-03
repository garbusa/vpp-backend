package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.StorageDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.StorageException;
import de.uol.vpp.masterdata.domain.services.IStorageService;
import de.uol.vpp.masterdata.domain.services.StorageServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/storage", produces = MediaType.APPLICATION_JSON_VALUE)
public class StorageController {

    private final IStorageService service;
    private final ApplicationDomainConverter converter;

    @GetMapping(path = "/by/dpp/{" +
            "decentralizedPowerPlantId}")
    public ResponseEntity<?> getAllStoragesByDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Speicher war erfolgreich",
                            service.getAllByDecentralizedPowerPlantId(decentralizedPowerPlantId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/by/household/{" +
            "householdId}")
    public ResponseEntity<?> getAllStoragesByHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Speicher war erfolgreich",
                            service.getAllByHouseholdId(householdId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{storageId}")
    public ResponseEntity<?> getOneStorage(@PathVariable String storageId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage eines Speichers war erfolgreich", converter.toApplication(service.get(storageId)))
                    , HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/dpp/{decentralizedPowerPlantId}")
    public ResponseEntity<?> saveStorageWithDecentralizedPowerPlant(@RequestBody StorageDTO dto,
                                                                    @PathVariable String decentralizedPowerPlantId) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Speicher wurde erfolgreich erstellt und einem DK zugewiesen", null));
        } catch (StorageServiceException | StorageException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/household/{householdId}")
    public ResponseEntity<?> saveStorageWithHousehold(@RequestBody StorageDTO dto,
                                                      @PathVariable String householdId) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Speicher wurde erfolgreich erstellt und einem Haushalt zugewiesen", null));
        } catch (StorageServiceException | StorageException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{storageId}")
    public ResponseEntity<?> deleteStorage(@PathVariable String storageId, @RequestParam String virtualPowerPlantId) {
        try {
            service.delete(storageId, virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "Speicher wurde erfolgreich gelöscht", null));
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{storageId}")
    public ResponseEntity<?> updateStorage(@PathVariable String storageId, @RequestBody StorageDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(storageId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "Speicher wurde erfolgreich aktualisiert", null));
        } catch (StorageServiceException | StorageException e) {
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
