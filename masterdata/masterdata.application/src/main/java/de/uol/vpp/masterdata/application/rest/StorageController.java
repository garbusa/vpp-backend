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
            "dppBusinessKey}")
    public ResponseEntity<?> getAllStoragesByDecentralizedPowerPlant(@PathVariable String dppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "storages successfully fetched.",
                            service.getAllByDecentralizedPowerPlantId(dppBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/by/household/{" +
            "householdBusinessKey}")
    public ResponseEntity<?> getAllStoragesByHousehold(@PathVariable String householdBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "storages successfully fetched.",
                            service.getAllByHouseholdId(householdBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneStorage(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "storage successfully fetched", converter.toApplication(service.get(businessKey)))
                    , HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/dpp/{dppBusinessKey}")
    public ResponseEntity<?> saveStorageWithDecentralizedPowerPlant(@RequestBody StorageDTO dto,
                                                                    @PathVariable String dppBusinessKey) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), dppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "storage successfully created and assigned to dpp", null));
        } catch (StorageServiceException | StorageException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/household/{householdBusinessKey}")
    public ResponseEntity<?> saveStorageWithHousehold(@RequestBody StorageDTO dto,
                                                      @PathVariable String householdBusinessKey) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "storage successfully created and assigned to household", null));
        } catch (StorageServiceException | StorageException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteStorage(@PathVariable String businessKey, @RequestParam String vppBusinessKey) {
        try {
            service.delete(businessKey, vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "Storage successfully deleted", null));
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{businessKey}")
    public ResponseEntity<?> updateStorage(@PathVariable String businessKey, @RequestBody StorageDTO newDto, @RequestParam String vppBusinessKey) {
        try {
            service.update(businessKey, converter.toDomain(newDto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "storage successfully updated", null));
        } catch (StorageServiceException | StorageException e) {
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
