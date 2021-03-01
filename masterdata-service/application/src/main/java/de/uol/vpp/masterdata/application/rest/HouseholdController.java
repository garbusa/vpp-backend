package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.HouseholdDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.HouseholdException;
import de.uol.vpp.masterdata.domain.services.HouseholdServiceException;
import de.uol.vpp.masterdata.domain.services.IHouseholdService;
import lombok.RequiredArgsConstructor;
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
    private final ApplicationEntityConverter converter;

    @GetMapping(path = "/by/vpp/{vppBusinessKey}")
    public ResponseEntity<?> getAllHouseholdsByVirtualPowerPlantId(@PathVariable String vppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "households successfully fetched.",
                            service.getAllByVppId(vppBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneHousehold(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "household successfully fetched",
                            converter.toApplication(service.get(businessKey)))
                    , HttpStatus.OK);
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by/vpp/{vppBusinessKey}")
    public ResponseEntity<?> saveHousehold(@RequestBody HouseholdDTO dto,
                                           @PathVariable String vppBusinessKey) {
        try {
            service.save(converter.toDomain(dto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "household successfully created and assigned",
                    service.get(dto.getHouseholdId())
            ));
        } catch (HouseholdServiceException | HouseholdException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteHousehold(@PathVariable String businessKey, String vppBusinessKey) {
        try {
            service.delete(businessKey, vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "household successfully deleted", null));
        } catch (HouseholdServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{businessKey}")
    public ResponseEntity<?> updateHousehold(@PathVariable String businessKey, @RequestBody HouseholdDTO newDto, @RequestParam String vppBusinessKey) {
        try {
            service.update(businessKey, converter.toDomain(newDto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "household successfully updated",
                    converter.toApplication(service.get(newDto.getHouseholdId()))));
        } catch (HouseholdServiceException | HouseholdException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

}
