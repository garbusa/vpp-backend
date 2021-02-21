package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.ProducerDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.services.IProducerService;
import de.uol.vpp.masterdata.domain.services.ProducerServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/producer", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProducerController {

    private final IProducerService service;
    private final ApplicationEntityConverter converter;

    @Transactional
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
        }
    }

    @Transactional
    @GetMapping(path = "/by/household/{" +
            "householdBusinessKey}")
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
        }
    }

    @Transactional
    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneProducer(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "dpp successfully fetched", service.get(businessKey))
                    , HttpStatus.OK);
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/by/dpp")
    public ResponseEntity<?> saveProducerWithDecentralizedPowerPlant(@RequestBody ProducerDTO dto,
                                                                     @RequestParam String decentralizedPowerPlantBusinessKey) {
        try {
            service.saveWithDecentralizedPowerPlant(converter.toDomain(dto), decentralizedPowerPlantBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "producer successfully created and assigned to dpp", null
            ));
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/by/household")
    public ResponseEntity<?> saveProducerWithHousehold(@RequestBody ProducerDTO dto,
                                                       @RequestParam String householdBusinessKey) {
        try {
            service.saveWithHousehold(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "producer successfully created and assigned to household", null
            ));
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteProducer(@PathVariable String businessKey) {
        try {
            service.delete(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "producer successfully deleted", null));
        } catch (ProducerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

}
