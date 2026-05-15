package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.RequestDummyDTO;
import com.FedericoFunes.app_service.dtos.ResponseDummyDTO;
import com.FedericoFunes.app_service.services.DummyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dummy")
public class DummyController {
    private final DummyService dummyService;

    @GetMapping("")
    public ResponseEntity<List<ResponseDummyDTO>> getAllDummys() {
        return ResponseEntity.ok(dummyService.getAllDummys());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDummyDTO> getDummyById(@PathVariable Long id) {
        return ResponseEntity.ok(dummyService.getDummyById(id));
    }

    @PostMapping("")
    public ResponseEntity<ResponseDummyDTO> createDummy(@RequestBody RequestDummyDTO dummy) {
        return ResponseEntity.ok(dummyService.createDummy(dummy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDummyDTO> updateDummy(@RequestBody RequestDummyDTO dummy, @PathVariable Long id) {
        return ResponseEntity.ok(dummyService.updateDummy(dummy, id));
    }
}
