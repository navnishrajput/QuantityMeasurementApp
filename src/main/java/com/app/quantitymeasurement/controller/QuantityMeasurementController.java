package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantityMeasurementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityMeasurementController.class);
    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        this.service = service;
    }

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

    // @PostMapping("/compare")
    // public ResponseEntity<QuantityDTO> compare(@RequestBody QuantityRequest request) {
    //     QuantityDTO result = service.compare(request.getDto1(), request.getDto2());
    //     return ResponseEntity.ok(result);
    // }

    private void displayResult(QuantityDTO result, String operation) {
        System.out.println("=== " + operation + " Result ===");
        if (result.hasError()) {
            System.out.println("Error: " + result.getErrorMessage());
        } else {
            System.out.println(result.toString());
        }
    }
}