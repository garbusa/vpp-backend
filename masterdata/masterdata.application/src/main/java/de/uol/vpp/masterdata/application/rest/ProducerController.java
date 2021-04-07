package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.ProducerDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.application.payload.ProducerStatusUpdateRequest;
import de.uol.vpp.masterdata.domain.exceptions.ProducerException;
import de.uol.vpp.masterdata.domain.services.IProducerService;
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
@RequestMapping(path = "/producer", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProducerController {

    private final IProducerService service;
    private final ApplicationEntityConverter converter;

    @GetMapping(path = "/by/dpp/{" +
            "dppBusinessKey}")
    public ResponseEntity<?> getAllProducersByDecentralizedPowerPlant(@PathVariable String dppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "producers successfully fetched.",
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
            "householdTimestamp}")
    public ResponseEntity<?> getAllProducersByHousehold(@PathVariable String householdBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "producers successfully fetched.",
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
    public ResponseEntity<?> getOneProducer(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "producer successfully fetched", converter.toApplication(service.get(businessKey)))
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

    //todo wenn vpp schon ein producer hat, aber published => error (selbe für storage)
    @PostMapping("/by/dpp/{dppBusinessKey}")
    public ResponseEntity<?> saveProducerWithDecentralizedPowerPlant(@RequestBody ProducerDTO dto, @PathVariable String dppBusinessKey) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), dppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "producer successfully created and assigned to dpp", null));
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
    public ResponseEntity<?> saveProducerWithHousehold(@RequestBody ProducerDTO dto,
                                                       @PathVariable String householdBusinessKey) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "producer successfully created and assigned to household", null));
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
    public ResponseEntity<?> deleteProducer(@PathVariable String businessKey, @RequestParam String vppBusinessKey) {
        try {
            service.delete(businessKey, vppBusinessKey);
            return ResponseEntity.ok().body(
                    new ApiResponse(true, false, "producer successfully deleted", null));
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

    @PutMapping(path = "/status")
    public ResponseEntity<?> updateStatus(@RequestBody ProducerStatusUpdateRequest request) {
        try {
            service.updateStatus(
                    request.getBusinessKey(),
                    request.getCapacity(),
                    request.isRunning(),
                    request.getVppBusinessKey()
            );
            return ResponseEntity.ok(new ApiResponse(
                    true, false, "consumer status successfully toggled", null));
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
    public ResponseEntity<?> updateProducer(@PathVariable String businessKey, @RequestBody ProducerDTO newDto, @RequestParam String vppBusinessKey) {
        try {
            service.update(businessKey, converter.toDomain(newDto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false,
                    "producer successfully updated", null));
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
