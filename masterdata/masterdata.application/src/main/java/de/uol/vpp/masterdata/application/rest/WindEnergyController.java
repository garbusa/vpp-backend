package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.WindEnergyDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;
import de.uol.vpp.masterdata.domain.services.IWindEnergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST-Ressource für Windkraftanlagen
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/wind", produces = MediaType.APPLICATION_JSON_VALUE)
public class WindEnergyController {

    private final IWindEnergyService service;
    private final ApplicationDomainConverter converter;

    /**
     * Hole alle Windkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste Windkraftanlagen
     */
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

    /**
     * Hole alle Windkraftanlagen eines Haushalts
     *
     * @param householdId Id des Haushalts
     * @return Liste von Windkraftanlagen
     */
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

    /**
     * Hole eine spezifische Windkraftanlage
     *
     * @param windEnergyId Id der Windkraftanlage
     * @return Windkraftanlage
     */
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

    /**
     * Persistiert Windkraftanlage und weist es einem DK zu
     *
     * @param dto                       zu speichernde Windkraftanlage
     * @param decentralizedPowerPlantId Id des DK
     * @return ApiResponse ohne Daten
     */
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

    /**
     * Persistiert Windkraftanlage und weist es einem Haushalt zu
     *
     * @param dto         zu speichernde Windkraftanlage
     * @param householdId Id des Haushalts
     * @return ApiResponse ohne Daten
     */
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

    /**
     * Löscht Windkraftanlage
     *
     * @param windEnergyId        Id der Windkraftanlage
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
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

    /**
     * Aktualisiert Windkraftanlage
     *
     * @param windEnergyId        Id der Windkraftanlage
     * @param newDto              aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
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
