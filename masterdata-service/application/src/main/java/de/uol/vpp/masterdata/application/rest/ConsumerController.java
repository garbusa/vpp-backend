package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.ConsumerDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.services.ConsumerServiceException;
import de.uol.vpp.masterdata.domain.services.IConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/consumer", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsumerController {

    private final IConsumerService service;
    private final ApplicationEntityConverter converter;


    @Transactional
    @GetMapping(path = "/by/household/{" +
            "householdBusinessKey}")
    public ResponseEntity<?> getAllConsumersByHousehold(@PathVariable String householdBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "consumers successfully fetched.",
                            service.getAllByHouseholdId(householdBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneConsumer(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "consumer successfully fetched", service.get(businessKey))
                    , HttpStatus.OK);
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("/by/household")
    public ResponseEntity<?> saveConsumerWithHousehold(@RequestBody ConsumerDTO dto,
                                                       @RequestParam String householdBusinessKey) {
        try {
            service.save(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "consumer successfully created and assigned to household", null
            ));
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteConsumer(@PathVariable String businessKey) {
        try {
            service.delete(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "consumer successfully deleted", null));
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

}
