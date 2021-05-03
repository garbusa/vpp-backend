package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.WaterEnergyDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.services.IWaterEnergyService;
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
@RequestMapping(path = "/water", produces = MediaType.APPLICATION_JSON_VALUE)
public class WaterEnergyController {

    private final IWaterEnergyService service;
    private final ApplicationDomainConverter converter;

    @GetMapping(path = "/by/dpp/{" +
            "decentralizedPowerPlantId}")
    public ResponseEntity<?> getAllWaterEnergysByDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Wasserkraftanlagen war erfolgreich",
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
    public ResponseEntity<?> getAllWaterEnergysByHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Wasserkraftanlagen war erfolgreich",
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

    @GetMapping(path = "/{waterEnergyId}")
    public ResponseEntity<?> getOneWaterEnergy(@PathVariable String waterEnergyId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage einer Wasserkraftanlage war erfolgreich", converter.toApplication(service.get(waterEnergyId)))
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

    //todo wenn vpp schon ein waterEnergy hat, aber published => error (selbe für storage)
    @PostMapping("/by/dpp/{decentralizedPowerPlantId}")
    public ResponseEntity<?> saveWaterEnergyWithDecentralizedPowerPlant(@RequestBody WaterEnergyDTO dto, @PathVariable String decentralizedPowerPlantId) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Wasserkraftanlage wurde erfolgreich angelegt und einem DK zugewiesen", null));
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
    public ResponseEntity<?> saveWaterEnergyWithHousehold(@RequestBody WaterEnergyDTO dto,
                                                          @PathVariable String householdId) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Wasserkraftanlage wurde erfolgreich angelegt und einem Haushalt zugewiesen", null));
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

    @DeleteMapping(path = "/{waterEnergyId}")
    public ResponseEntity<?> deleteWaterEnergy(@PathVariable String waterEnergyId, @RequestParam String virtualPowerPlantId) {
        try {
            service.delete(waterEnergyId, virtualPowerPlantId);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, false, "Wasserkraftanlage wurde erfolgreich gelöscht", null));
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

    @PutMapping(path = "/{waterEnergyId}")
    public ResponseEntity<?> updateWaterEnergy(@PathVariable String waterEnergyId, @RequestBody WaterEnergyDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(waterEnergyId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    "Wasserkraftanlage wurde erfolgreich aktualisiert", null));
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
