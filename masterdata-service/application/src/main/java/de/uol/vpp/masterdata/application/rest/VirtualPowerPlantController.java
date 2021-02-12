package de.uol.vpp.masterdata.application.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/vpp", produces = MediaType.APPLICATION_JSON_VALUE)
public class VirtualPowerPlantController {

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok("hello");
    }

}
