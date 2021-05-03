package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.WindEnergyDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.services.IWindEnergyService;
import de.uol.vpp.masterdata.domain.services.ProducerServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/wind", produces = MediaType.APPLICATION_JSON_VALUE)
public class WindEnergyController {

    private final IWindEnergyService service;
    private final ApplicationDomainConverter converter;

    @GetMapping(path = "/by/dpp/{" +
            "decentralizedPowerPlantId}")
    public ResponseEntity<?> getAllWindEnergysByDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Windkraftanlagen war erfolgreich",
                            service.getAllByDecentralizedPowerPlantId(decentralizedPowerPlantId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/by/household/{" +
            "householdId}")
    public ResponseEntity<?> getAllWindEnergysByHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Windkraftanlagen war erfolgreich",
                            service.getAllByHouseholdId(householdId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{windEnergyId}")
    public ResponseEntity<?> getOneWindEnergy(@PathVariable String windEnergyId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage einer Windkraftanlage war erfolgreich", converter.toApplication(service.get(windEnergyId)))
                    , HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    //todo wenn vpp schon ein windEnergy hat, aber published => error (selbe für storage)
    @PostMapping("/by/dpp/{decentralizedPowerPlantId}")
    public ResponseEntity<?> saveWindEnergyWithDecentralizedPowerPlant(@RequestBody WindEnergyDTO dto, @PathVariable String decentralizedPowerPlantId) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Windkraftanlage wurde erfolgreich angelegt und einem DK zugewiesen", null));
        } catch (ProducerServiceException | ProducerException e) {
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
    public ResponseEntity<?> saveWindEnergyWithHousehold(@RequestBody WindEnergyDTO dto,
                                                         @PathVariable String householdId) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Windkraftanlage wurde erfolgreich angelegt und einem Haushalt zugewiesen", null));
        } catch (ProducerServiceException | ProducerException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{windEnergyId}")
    public ResponseEntity<?> deleteWindEnergy(@PathVariable String windEnergyId, @RequestParam String virtualPowerPlantId) {
        try {
            service.delete(windEnergyId, virtualPowerPlantId);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, false, "Windkraftanlage wurde erfolgreich gelöscht", null));
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{windEnergyId}")
    public ResponseEntity<?> updateWindEnergy(@PathVariable String windEnergyId, @RequestBody WindEnergyDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(windEnergyId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    "Windkraftanlage wurde erfolgreich aktualisiert", null));
        } catch (ProducerServiceException | ProducerException e) {
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
