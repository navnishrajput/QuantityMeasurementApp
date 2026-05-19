package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurement API", description = "Operations for quantity measurement comparison, conversion, and arithmetic")
public class QuantityMeasurementController {

    @Autowired
    private IQuantityMeasurementService service;

    @Operation(summary = "Compare two quantities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comparison successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/compare")
    public ResponseEntity<QuantityMeasurementDTO> performCompare(
            @Valid @RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    @Operation(summary = "Convert quantity")
    @PostMapping("/convert")
    public ResponseEntity<QuantityMeasurementDTO> performConvert(
            @Valid @RequestBody QuantityInputDTO input) {
        String targetUnit = input.getThatQuantityDTO().getUnit();
        return ResponseEntity.ok(service.convert(input.getThisQuantityDTO(), targetUnit));
    }

    @Operation(summary = "Add two quantities")
    @PostMapping("/add")
    public ResponseEntity<QuantityMeasurementDTO> performAdd(
            @Valid @RequestBody QuantityInputDTO input) {
        if (input.getTargetUnit() != null) {
            return ResponseEntity.ok(service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO(), input.getTargetUnit()));
        }
        return ResponseEntity.ok(service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    @Operation(summary = "Subtract two quantities")
    @PostMapping("/subtract")
    public ResponseEntity<QuantityMeasurementDTO> performSubtract(
            @Valid @RequestBody QuantityInputDTO input) {
        if (input.getTargetUnit() != null) {
            return ResponseEntity.ok(service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO(), input.getTargetUnit()));
        }
        return ResponseEntity.ok(service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    @Operation(summary = "Divide two quantities")
    @PostMapping("/divide")
    public ResponseEntity<QuantityMeasurementDTO> performDivide(
            @Valid @RequestBody QuantityInputDTO input) {
        return ResponseEntity.ok(service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    @Operation(summary = "Get history by operation")
    @GetMapping("/history/operation/{operation}")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByOperation(
            @Parameter(description = "Operation type") @PathVariable String operation) {
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    @Operation(summary = "Get history by type")
    @GetMapping("/history/type/{type}")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByType(
            @Parameter(description = "Measurement type") @PathVariable String type) {
        return ResponseEntity.ok(service.getHistoryByType(type));
    }

    @Operation(summary = "Get error history")
    @GetMapping("/history/errored")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        return ResponseEntity.ok(service.getErrorHistory());
    }

    @Operation(summary = "Get operation count")
    @GetMapping("/count/{operation}")
    public ResponseEntity<Long> getCountByOperation(
            @Parameter(description = "Operation type") @PathVariable String operation) {
        return ResponseEntity.ok(service.getCountByOperation(operation));
    }
}