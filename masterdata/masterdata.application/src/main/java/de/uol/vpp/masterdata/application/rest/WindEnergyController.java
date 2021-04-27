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
            "dppBusinessKey}")
    public ResponseEntity<?> getAllWindEnergysByDecentralizedPowerPlant(@PathVariable String dppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "windEnergys successfully fetched.",
                            service.getAllByDecentralizedPowerPlantId(dppBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/by/household/{" +
            "householdBusinessKey}")
    public ResponseEntity<?> getAllWindEnergysByHousehold(@PathVariable String householdBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "windEnergys successfully fetched.",
                            service.getAllByHouseholdId(householdBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneWindEnergy(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "windEnergy successfully fetched", converter.toApplication(service.get(businessKey)))
                    , HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    //todo wenn vpp schon ein windEnergy hat, aber published => error (selbe für storage)
    @PostMapping("/by/dpp/{dppBusinessKey}")
    public ResponseEntity<?> saveWindEnergyWithDecentralizedPowerPlant(@RequestBody WindEnergyDTO dto, @PathVariable String dppBusinessKey) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), dppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "windEnergy successfully created and assigned to dpp", null));
        } catch (ProducerServiceException | ProducerException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException sqlException) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, "data integrity error occured", null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/household/{householdBusinessKey}")
    public ResponseEntity<?> saveWindEnergyWithHousehold(@RequestBody WindEnergyDTO dto,
                                                         @PathVariable String householdBusinessKey) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "windEnergy successfully created and assigned to household", null));
        } catch (ProducerServiceException | ProducerException e) {
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
    public ResponseEntity<?> deleteWindEnergy(@PathVariable String businessKey, @RequestParam String vppBusinessKey) {
        try {
            service.delete(businessKey, vppBusinessKey);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, false, "windEnergy successfully deleted", null));
        } catch (ProducerServiceException e) {
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
    public ResponseEntity<?> updateWindEnergy(@PathVariable String businessKey, @RequestBody WindEnergyDTO newDto, @RequestParam String vppBusinessKey) {
        try {
            service.update(businessKey, converter.toDomain(newDto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    "windEnergy successfully updated", null));
        } catch (ProducerServiceException | ProducerException e) {
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
