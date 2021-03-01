package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.ConsumerDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.application.payload.ConsumerStatusUpdateRequest;
import de.uol.vpp.masterdata.domain.exceptions.ConsumerException;
import de.uol.vpp.masterdata.domain.services.ConsumerServiceException;
import de.uol.vpp.masterdata.domain.services.IConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/consumer", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsumerController {

    private final IConsumerService service;
    private final ApplicationEntityConverter converter;

    @GetMapping(path = "/by/household/{" +
            "businessKey}")
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

    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneConsumer(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "consumer successfully fetched",
                            converter.toApplication(service.get(businessKey)))
                    , HttpStatus.OK);
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/household/{householdBusinessKey}")
    public ResponseEntity<?> saveConsumerWithHousehold(@RequestBody ConsumerDTO dto,
                                                       @PathVariable String householdBusinessKey) {
        try {
            service.save(converter.toDomain(dto), householdBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "consumer successfully created and assigned to household",
                    converter.toApplication(service.get(dto.getConsumerId()))
            ));
        } catch (ConsumerServiceException | ConsumerException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteConsumer(@PathVariable String businessKey, @RequestParam String vppBusinessKey) {
        try {
            service.delete(businessKey, vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "consumer successfully deleted", null));
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/status")
    public ResponseEntity<?> updateStatus(@RequestBody ConsumerStatusUpdateRequest request) {
        try {
            service.updateStatus(request.getBusinessKey(), request.isRunning(), request.getVppBusinessKey());
            return ResponseEntity.ok(new ApiResponse(
                    true, false, "consumer status successfully updated",
                    converter.toApplication(service.get(request.getBusinessKey()))
            ));
        } catch (ConsumerServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{businessKey}")
    public ResponseEntity<?> updateConsumer(@PathVariable String businessKey, @RequestBody ConsumerDTO newDto, @RequestParam String vppBusinessKey) {
        try {
            service.update(businessKey, converter.toDomain(newDto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "consumer successfully updated",
                    converter.toApplication(service.get(newDto.getConsumerId()))));
        } catch (ConsumerServiceException | ConsumerException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

}
