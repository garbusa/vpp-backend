package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.StorageDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.services.IStorageService;
import de.uol.vpp.masterdata.domain.services.StorageServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/storage", produces = MediaType.APPLICATION_JSON_VALUE)
public class StorageController {

    private final IStorageService service;
    private final ApplicationEntityConverter converter;

    @Transactional
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
        }
    }

    @Transactional
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
        }
    }

    @Transactional
    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneStorage(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "storage successfully fetched", service.get(businessKey))
                    , HttpStatus.OK);
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/by/dpp")
    public ResponseEntity<?> saveStorageWithDecentralizedPowerPlant(@RequestBody StorageDTO dto,
                                                                    @RequestParam String decentralizedPowerPlantBusinessKey) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "storage successfully created and assigned to dpp", null
            ));
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/by/household")
    public ResponseEntity<?> saveStorageWithHousehold(@RequestBody StorageDTO dto,
                                                      @RequestParam String householdBusinessKey) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "storage successfully created and assigned to household", null
            ));
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteStorage(@PathVariable String businessKey) {
        try {
            service.delete(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "Storage successfully deleted", null));
        } catch (StorageServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

}
