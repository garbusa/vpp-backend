package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.WaterEnergyDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;
import de.uol.vpp.masterdata.domain.services.IWaterEnergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST-Ressource für Wasserkraftanlagen
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/water", produces = MediaType.APPLICATION_JSON_VALUE)
public class WaterEnergyController {

    private final IWaterEnergyService service;
    private final ApplicationDomainConverter converter;

    /**
     * Hole alle Wasserkraftanlagen eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste Wasserkraftanlagen
     */
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

    /**
     * Hole alle Wasserkraftanlagen eines Haushalts
     *
     * @param householdId Id des Haushalts
     * @return Liste von Wasserkraftanlagen
     */
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

    /**
     * Hole eine spezifische Wasserkraftanlage
     *
     * @param waterEnergyId Id der Wasserkraftanlage
     * @return Wasserkraftanlage
     */
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

    /**
     * Persistiert Wasserkraftanlage und weist es einem DK zu
     *
     * @param dto                       zu speichernde Wasserkraftanlage
     * @param decentralizedPowerPlantId Id des DK
     * @return ApiResponse ohne Daten
     */
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


    /**
     * Persistiert Wasserkraftanlage und weist es einem Haushalt zu
     *
     * @param dto         zu speichernde Wasserkraftanlage
     * @param householdId Id des Haushalts
     * @return ApiResponse ohne Daten
     */
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

    /**
     * Löscht Wasserkraftanlage
     *
     * @param waterEnergyId       Id der Wasserkraftanlage
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
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

    /**
     * Aktualisiert Wasserkraftanlage
     *
     * @param waterEnergyId       Id der Wasserkraftanlage
     * @param newDto              aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
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
