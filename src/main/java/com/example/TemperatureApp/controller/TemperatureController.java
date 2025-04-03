package com.example.TemperatureApp.controller;

import com.example.TemperatureApp.model.SensorAverageProjection;
import com.example.TemperatureApp.service.TemperatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/temperature")
@RequiredArgsConstructor
@Tag(name = "Temperature API", description = "API for managing temperature data")
public class TemperatureController {

    private final TemperatureService temperatureService;

    @GetMapping("/average/{sensorId}")
    @Operation(summary = "Get average temperature for a sensor",
            description = "Returns the average temperature for the specified sensor ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved average temperature",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class)))
    @ApiResponse(responseCode = "404", description = "Sensor not found or no data available (if service handled differently)")
    public ResponseEntity<Double> getAverageTemperature(
            @Parameter(description = "ID of the sensor", required = true, example = "sensor-001") @PathVariable String sensorId) {
        double average = temperatureService.getAverageTemperature(sensorId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/sensors/above/{threshold}")
    @Operation(summary = "Get sensors with average temperature above threshold",
            description = "Returns a list of sensors whose average temperature is above the specified threshold.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of sensors",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorAverageProjection.class)))
    public ResponseEntity<List<SensorAverageProjection>> getSensorsAboveThreshold(
            @Parameter(description = "Temperature threshold", required = true, example = "25.5") @PathVariable double threshold) {
        List<SensorAverageProjection> sensors = temperatureService.getSensorsAboveThreshold(threshold);
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/readings/above")
    @Operation(summary = "Count readings above temperature within a time range",
            description = "Returns the count of temperature readings exceeding the specified threshold within the given time range.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the count",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    @ApiResponse(responseCode = "400", description = "Invalid date format provided") // Example for bad request
    public ResponseEntity<Long> getReadingsAboveTemperature(
            @Parameter(description = "Temperature threshold", required = true, example = "30.0")
            @RequestParam double tempThreshold,

            @Parameter(description = "Start time (ISO 8601 format, e.g., yyyy-MM-dd'T'HH:mm:ss)", required = true, example = "2023-10-26T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime, // Use Spring's date parsing

            @Parameter(description = "End time (ISO 8601 format, e.g., yyyy-MM-dd'T'HH:mm:ss)", required = true, example = "2023-10-27T18:30:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) { // Use Spring's date parsing

        long count = temperatureService.getReadingsAboveTemperature(tempThreshold, startTime, endTime);
        return ResponseEntity.ok(count);
    }
}