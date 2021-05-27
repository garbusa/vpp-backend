package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationDomainConverter;
import de.uol.vpp.masterdata.application.dto.OtherEnergyDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.exceptions.ProducerServiceException;
import de.uol.vpp.masterdata.domain.services.IOtherEnergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST-Ressource für alternative Erzeugungsanlagen
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/other", produces = MediaType.APPLICATION_JSON_VALUE)
public class OtherEnergyController {

    private final IOtherEnergyService service;
    private final ApplicationDomainConverter converter;

    /**
     * Hole alle alternativen Erzeugungsanlage eines DK
     *
     * @param decentralizedPowerPlantId Id des DK
     * @return Liste alternativer Erzeugungsanlagen
     */
    @GetMapping(path = "/by/dpp/{" +
            "decentralizedPowerPlantId}")
    public ResponseEntity<?> getAllOtherEnergysByDecentralizedPowerPlant(@PathVariable String decentralizedPowerPlantId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller alternativen Erzeugungsanlagen war erfolgreich",
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
     * Hole alle alternativen Erzeugungsanlagen eines Haushalts
     *
     * @param householdId Id des Haushalts
     * @return Liste alternative Erzeugungsanlagen
     */
    @GetMapping(path = "/by/household/{" +
            "householdId}")
    public ResponseEntity<?> getAllOtherEnergysByHousehold(@PathVariable String householdId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage aller alternativen Erzeugungsanlagen war erfolgreich",
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
     * Hole eine spezifische alternative Erzeugungsanlage
     *
     * @param otherEnergyId Id der alternativen Erzeugungsanlage
     * @return alternative Erzeugungsanlage
     */
    @GetMapping(path = "/{otherEnergyId}")
    public ResponseEntity<?> getOneOtherEnergy(@PathVariable String otherEnergyId) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "Abfrage einer alternativen Erzeugungsanlage war erfolgreich", converter.toApplication(service.get(otherEnergyId)))
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
     * Persistiert Erzeugungsanlage und weist es einem DK zu
     *
     * @param dto                       zu speichernde Erzeugungsanlage
     * @param decentralizedPowerPlantId Id des DK
     * @return ApiResponse ohne Daten
     */
    @PostMapping("/by/dpp/{decentralizedPowerPlantId}")
    public ResponseEntity<?> saveOtherEnergyWithDecentralizedPowerPlant(@RequestBody OtherEnergyDTO dto, @PathVariable String decentralizedPowerPlantId) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Alternative Erzeugungsanlage wurde erfolgreich angelegt und einem DK zugewiesen", null));
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
     * Persistiert Erzeugungsanlage und weist es einem Haushalt zu
     *
     * @param dto         zu speichernde Erzeugungsanlage
     * @param householdId Id des Haushalts
     * @return ApiResponse ohne Daten
     */
    @PostMapping("/by/household/{householdId}")
    public ResponseEntity<?> saveOtherEnergyWithHousehold(@RequestBody OtherEnergyDTO dto,
                                                          @PathVariable String householdId) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdId);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "Alternative Erzeugungsanlage wurde erfolgreich angelegt und einem Haushalt zugewiesen", null));
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
     * Löscht alternative Erzeugungsanlage
     *
     * @param otherEnergyId       Id der alternativen Erzeugungsanlage
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @DeleteMapping(path = "/{otherEnergyId}")
    public ResponseEntity<?> deleteOtherEnergy(@PathVariable String otherEnergyId, @RequestParam String virtualPowerPlantId) {
        try {
            service.delete(otherEnergyId, virtualPowerPlantId);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, false, "Alternative Erzeugungsanlage wurde erfolgreich gelöscht", null));
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
     * Aktualisiert alternative Erzeugungsanlage
     *
     * @param otherEnergyId       Id der alternativen Erzeugungsanlage
     * @param newDto              aktualisierte Daten
     * @param virtualPowerPlantId Id des VK
     * @return ApiResponse ohne Daten
     */
    @PutMapping(path = "/{otherEnergyId}")
    public ResponseEntity<?> updateOtherEnergy(@PathVariable String otherEnergyId, @RequestBody OtherEnergyDTO newDto, @RequestParam String virtualPowerPlantId) {
        try {
            service.update(otherEnergyId, converter.toDomain(newDto), virtualPowerPlantId);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    "Alternative Erzeugungsanlage wurde erfolgreich aktualisiert", null));
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
