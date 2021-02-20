package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.HouseholdDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.services.HouseholdServiceException;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/household", produces = MediaType.APPLICATION_JSON_VALUE)
public class HouseholdController {

    private final IHouseholdService service;
    private final ApplicationEntityConverter converter;

    @Transactional
    @GetMapping(path = "/by/vpp/{vppBusinessKey}")
    public ResponseEntity<?> getAllHouseholdsByVirtualPowerPlantId(@PathVariable String vppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "dpp's successfully fetched.",
                            service.getAllByVppId(vppBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneHousehold(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "dpp successfully fetched", service.get(businessKey))
                    , HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> saveHousehold(@RequestBody HouseholdDTO dto,
                                           @RequestParam String virtualPowerPlantBusinessKey) {
        try {
            service.save(converter.toDomain(dto), virtualPowerPlantBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "dpp successfully created and assigned", null
            ));
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }

    }

    @Transactional
    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteHousehold(@PathVariable String businessKey) {
        try {
            service.delete(businessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "household successfully deleted", null));
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

}
