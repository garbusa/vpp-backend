package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.VirtualPowerPlantDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.VirtualPowerPlantException;
import de.uol.vpp.masterdata.domain.services.IVirtualPowerPlantService;
import de.uol.vpp.masterdata.domain.services.VirtualPowerPlantServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(path = "/vpp", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VirtualPowerPlantController {

    private final IVirtualPowerPlantService service;
    private final ApplicationEntityConverter converter;

    @GetMapping
    public ResponseEntity<?> getAllVirtualPowerPlants() {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "vpp's successfully fetched",
                            service.getAll().stream().map(converter::toApplication).collect(Collectors.toList()))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneVirtualPowerPlant(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "vpp successfully fetched",
                            converter.toApplication(service.get(businessKey)))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveVirtualPowerPlant(@RequestBody VirtualPowerPlantDTO dto) {
        try {
            if (dto.isPublished()) {
                throw new VirtualPowerPlantException("vpp can't be initially published");
            }
            service.save(converter.toDomain(dto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "" +
                    "vpp successfully created", null));
        } catch (VirtualPowerPlantServiceException | VirtualPowerPlantException e) {
            log.error(e);
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
    public ResponseEntity<?> deleteVirtualPowerPlant(@PathVariable String businessKey) {
        try {
            service.delete(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "vpp successfully deleted", null));
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{businessKey}/publish")
    public ResponseEntity<?> publishVirtualPowerPlant(@PathVariable String businessKey) {
        try {
            service.publish(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "vpp successfully published",
                    null));
        } catch (VirtualPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(path = "/{businessKey}/unpublish")
    public ResponseEntity<?> unpublishVirtualPowerPlant(@PathVariable String businessKey) {
        try {
            service.unpublish(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "vpp successfully unpublished",
                    null));
        } catch (VirtualPowerPlantServiceException e) {
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
    public ResponseEntity<?> updateVirtualPowerPlant(@PathVariable String businessKey, @RequestBody VirtualPowerPlantDTO newDto) {
        try {
            service.update(businessKey, converter.toDomain(newDto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "vpp successfully updated",
                    null));
        } catch (VirtualPowerPlantServiceException | VirtualPowerPlantException e) {
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
