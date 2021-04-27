package de.uol.vpp.production.application.rest;

import de.uol.vpp.production.application.ApplicationDomainConverter;
import de.uol.vpp.production.application.payload.ApiResponse;
import de.uol.vpp.production.domain.exceptions.ProductionServiceException;
import de.uol.vpp.production.domain.services.IProductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/production", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class ProductionController {

    private final IProductionService productionService;
    private final ApplicationDomainConverter converter;

    @GetMapping(path = "/{actionRequestBusinessKey}")
    public ResponseEntity<?> getAllProductionsByVirtualPowerPlantIdAndTimestamp(@PathVariable String actionRequestBusinessKey) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, false, "productions successfully fetched.",
                            productionService.getProductionsByActionRequestId(actionRequestBusinessKey)
                                    .stream()
                                    .map(converter::toApplication)
                                    .collect(Collectors.toList())
                    ), HttpStatus.OK);
        } catch (ProductionServiceException e) {
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
