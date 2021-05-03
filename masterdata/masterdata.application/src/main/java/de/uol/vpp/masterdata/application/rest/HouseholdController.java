package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.HouseholdDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.services.HouseholdServiceException;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/household", produces = MediaType.APPLICATION_JSON_VALUE)
public class HouseholdController {

    private final IHouseholdService service;
    private final ApplicationDomainConverter converter;

    @GetMapping(path = "/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> getAllHouseholdsByVirtualPowerPlantId(@PathVariable String virtualPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller Haushalte war erfolgreich",
                            service.getAllByVppId(virtualPowerPlantId)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{householdId}")
    public ResponseEntity<?> getOneHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage eines Haushaltes war erfolgreich",
                            converter.toApplication(service.get(householdId)))
                    , HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/vpp/{virtualPowerPlantId}")
    public ResponseEntity<?> saveHousehold(@RequestBody HouseholdDTO dto,
                                           @PathVariable String virtualPowerPlantId) {
        try {
            service.save(converter.toDomain(dto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Haushalt wurde erfolgreich angelegt und einem VK zugewiesen", null));
        } catch (HouseholdServiceException | HouseholdException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping(path = "/{householdId}")
    public ResponseEntity<?> deleteHousehold(@PathVariable String householdId, String virtualPowerPlantId) {
        try {
            service.delete(householdId, virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "Haushalt wurde erfolgreich gelöscht", null));
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "Es ist ein Datenintegritätsfehler geschehen", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{householdId}")
    public ResponseEntity<?> updateHousehold(@PathVariable String householdId, @RequestBody HouseholdDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(householdId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    "Haushalt wurde erfolgreich aktualisiert", null));
        } catch (HouseholdServiceException | HouseholdException e) {
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
