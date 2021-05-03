package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
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
    private final ApplicationDomainConverter converter;

    @GetMapping
    public ResponseEntity<?> getAllVirtualPowerPlants() {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller VKs war erfolgreich",
                            service.getAll().stream().map(converter::toApplication).collect(Collectors.toList()))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveVirtualPowerPlants() {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller veröffentlichen VKs war erfolgreich",
                            service.getAllActives().stream().map(converter::toApplication).collect(Collectors.toList()))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{virtualPowerPlantId}")
    public ResponseEntity<?> getOneVirtualPowerPlant(@PathVariable String virtualPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage eines VKs war erfolgreich",
                            converter.toApplication(service.get(virtualPowerPlantId)))
                    , HttpStatus.OK);
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveVirtualPowerPlant(@RequestBody VirtualPowerPlantDTO dto) {
        try {
            if (dto.isPublished()) {
                throw new VirtualPowerPlantException("Ein VK kann nicht intial veröffentlicht sein");
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
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{virtualPowerPlantId}")
    public ResponseEntity<?> deleteVirtualPowerPlant(@PathVariable String virtualPowerPlantId) {
        try {
            service.delete(virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "Alle VKs wurden erfolgreich gelöscht", null));
        } catch (VirtualPowerPlantServiceException e) {
            log.error(e);
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{virtualPowerPlantId}/publish")
    public ResponseEntity<?> publishVirtualPowerPlant(@PathVariable String virtualPowerPlantId) {
        try {
            service.publish(virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "VK wurde erfolgreich veröffentlicht",
                    null));
        } catch (VirtualPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(path = "/{virtualPowerPlantId}/unpublish")
    public ResponseEntity<?> unpublishVirtualPowerPlant(@PathVariable String virtualPowerPlantId) {
        try {
            service.unpublish(virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "VK wurde erfolgreich unveröffentlicht",
                    null));
        } catch (VirtualPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{virtualPowerPlantId}")
    public ResponseEntity<?> updateVirtualPowerPlant(@PathVariable String virtualPowerPlantId, @RequestBody VirtualPowerPlantDTO newDto) {
        try {
            service.update(virtualPowerPlantId, converter.toDomain(newDto));
            return ResponseEntity.ok().body(new ApiResponse(true, false, "VK wurde erfolgreich aktualisiert",
                    null));
        } catch (VirtualPowerPlantServiceException | VirtualPowerPlantException e) {
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
