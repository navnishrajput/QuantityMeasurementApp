package com.quantity.measurement.controller;

import com.quantity.measurement.dto.QuantityDTO;
import com.quantity.measurement.service.IQuantityMeasurementService;

// UC15: Controller layer - Facade pattern over service, REST-ready design
public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

    // UC15: Constructor injection - service provided by factory
    public QuantityMeasurementController(IQuantityMeasurementService service) {
        this.service = service;
    }

    // UC15: performXXX methods - designed for future @PostMapping annotations
    public void performCompare(QuantityDTO dto1, QuantityDTO dto2) {
        QuantityDTO result = service.compare(dto1, dto2);
        displayResult(result, "COMPARE");
    }

    public void performConvert(QuantityDTO dto, String targetUnit) {
        QuantityDTO result = service.convert(dto, targetUnit);
        displayResult(result, "CONVERT");
    }

    public void performAdd(QuantityDTO dto1, QuantityDTO dto2) {
        QuantityDTO result = service.add(dto1, dto2);
        displayResult(result, "ADD");
    }

    public void performAdd(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        QuantityDTO result = service.add(dto1, dto2, targetUnit);
        displayResult(result, "ADD");
    }

    public void performSubtract(QuantityDTO dto1, QuantityDTO dto2) {
        QuantityDTO result = service.subtract(dto1, dto2);
        displayResult(result, "SUBTRACT");
    }

    public void performSubtract(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        QuantityDTO result = service.subtract(dto1, dto2, targetUnit);
        displayResult(result, "SUBTRACT");
    }

    public void performDivide(QuantityDTO dto1, QuantityDTO dto2) {
        QuantityDTO result = service.divide(dto1, dto2);
        displayResult(result, "DIVIDE");
    }

    // UC15: Future REST endpoints - reserved for Spring MVC migration
    // @PostMapping("/compare")
    // public ResponseEntity<QuantityDTO> compare(@RequestBody QuantityRequest request) {
    //     QuantityDTO result = service.compare(request.getDto1(), request.getDto2());
    //     return ResponseEntity.ok(result);
    // }

    // UC15: Display helper - formats output for console, future: returns ResponseEntity
    private void displayResult(QuantityDTO result, String operation) {
        System.out.println("=== " + operation + " Result ===");
        if (result.hasError()) {
            System.out.println("Error: " + result.getErrorMessage());
        } else {
            System.out.println(result.toString());
        }
    }
}