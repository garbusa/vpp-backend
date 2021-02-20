package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.VirtualPowerPlantDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.services.IVirtualPowerPlantService;
import de.uol.vpp.masterdata.domain.services.VirtualPowerPlantServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/vpp", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VirtualPowerPlantController {

    private final IVirtualPowerPlantService service;
    private final ApplicationEntityConverter converter;

    @Transactional
    @GetMapping
    public ResponseEntity<?> getAllVirtualPowerPlants() {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "vpp's successfully fetched",
                            service.getAll().stream().map(converter::toApplication).collect(Collectors.toList()))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneVirtualPowerPlant(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "vpp successfully fetched", service.get(businessKey))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> saveVirtualPowerPlant(@RequestBody VirtualPowerPlantDTO dto) {
        try {
            service.save(converter.toDomain(dto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "" +
                    "vpp successfully created", null));
        } catch (VirtualPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteVirtualPowerPlant(@PathVariable String businessKey) {
        try {
            service.delete(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "vpp successfully deleted", null));
        } catch (VirtualPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }
}
