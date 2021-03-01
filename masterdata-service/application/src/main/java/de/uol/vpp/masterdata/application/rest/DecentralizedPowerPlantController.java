package de.uol.vpp.masterdata.application.rest;

import de.uol.vpp.masterdata.application.ApplicationEntityConverter;
import de.uol.vpp.masterdata.application.dto.DecentralizedPowerPlantDTO;
import de.uol.vpp.masterdata.application.payload.ApiResponse;
import de.uol.vpp.masterdata.domain.exceptions.DecentralizedPowerPlantException;
import de.uol.vpp.masterdata.domain.services.DecentralizedPowerPlantServiceException;
import de.uol.vpp.masterdata.domain.services.IDecentralizedPowerPlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/dpp", produces = MediaType.APPLICATION_JSON_VALUE)
public class DecentralizedPowerPlantController {

    private final IDecentralizedPowerPlantService service;
    private final ApplicationEntityConverter converter;

    @GetMapping(path = "/by/vpp/{vppBusinessKey}")
    public ResponseEntity<?> getAllDecentralizedPowerPlantsByVirtualPowerPlantId(@PathVariable String vppBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "dpp's successfully fetched.",
                            service.getAllByVppId(vppBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (DecentralizedPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(false, false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/{businessKey}")
    public ResponseEntity<?> getOneDecentralizedPowerPlant(@PathVariable String businessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "dpp successfully fetched",
                            converter.toApplication(service.get(businessKey)))
                    , HttpStatus.OK);
        } catch (DecentralizedPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/by/vpp/{vppBusinessKey}")
    public ResponseEntity<?> saveDecentralizedPowerPlant(@RequestBody DecentralizedPowerPlantDTO dto,
                                                         @PathVariable String vppBusinessKey) {
        try {
            service.save(converter.toDomain(dto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(
                    true, false, "dpp successfully created and assigned",
                    converter.toApplication(service.get(dto.getDecentralizedPowerPlantId()))
            ));
        } catch (DecentralizedPowerPlantServiceException | DecentralizedPowerPlantException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{businessKey}")
    public ResponseEntity<?> deleteDecentralizedPowerPlant(@PathVariable String businessKey, @RequestParam String vppBusinessKey) {
        try {
            service.delete(businessKey, vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "dpp successfully deleted", null));
        } catch (DecentralizedPowerPlantServiceException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{businessKey}")
    public ResponseEntity<?> updateDecentralizedPowerPlant(@PathVariable String businessKey, @RequestBody DecentralizedPowerPlantDTO newDto, @RequestParam String vppBusinessKey) {
        try {
            service.update(businessKey, converter.toDomain(newDto), vppBusinessKey);
            return ResponseEntity.ok().body(new ApiResponse(true, false, "dpp successfully updated",
                    converter.toApplication(service.get(newDto.getDecentralizedPowerPlantId()))));
        } catch (DecentralizedPowerPlantServiceException | DecentralizedPowerPlantException e) {
            return new ResponseEntity<>(new ApiResponse(
                    false, false, e.getMessage(), null
            ), HttpStatus.NOT_FOUND);
        }
    }


}
