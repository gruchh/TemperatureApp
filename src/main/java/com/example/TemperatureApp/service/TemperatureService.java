package com.example.TemperatureApp.service;

import com.example.TemperatureApp.model.SensorAverageProjection; // Ensure this import is correct and the class exists
import com.example.TemperatureApp.repository.TemperatureEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Good practice for read-only methods

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Could be used for getAverageTemperature return type

@Service
@RequiredArgsConstructor
public class TemperatureService {

    private final TemperatureEntityRepository temperatureRepository;

    /**
     * Retrieves the average temperature for a specific sensor.
     *
     * @param sensorId The identifier of the sensor.
     * @return The average temperature, or 0.0 if no data is found.
     *         (Consider returning Optional<Double> for clearer semantics if the repository method is adapted).
     */
    @Transactional(readOnly = true) // Optimization hint for the transaction manager and JPA provider
    public double getAverageTemperature(String sensorId) {
        Double avg = temperatureRepository.findAverageTemperatureBySensorId(sensorId);
        return avg != null ? avg : 0.0;
    }

    /**
     * Finds sensors whose average temperature exceeds the specified threshold.
     * Uses a projection to return only necessary data.
     *
     * @param threshold The temperature threshold value.
     * @return A List of SensorAverageProjection objects, each containing sensorId and averageTemperature.
     */
    @Transactional(readOnly = true)
    public List<SensorAverageProjection> getSensorsAboveThreshold(double threshold) {
        // Directly return the result from the repository method using projection
        return temperatureRepository.findSensorsWithAverageTemperatureAboveProjection(threshold);
    }

    /**
     * Counts the number of temperature readings above a specified threshold within a given time range.
     *
     * @param tempThreshold The temperature threshold.
     * @param start         The start timestamp of the interval (inclusive).
     * @param end           The end timestamp of the interval (inclusive).
     * @return The total count of readings matching the criteria.
     */
    @Transactional(readOnly = true)
    public long getReadingsAboveTemperature(double tempThreshold, LocalDateTime start, LocalDateTime end) {
        Long count = temperatureRepository.countReadingsAboveTemperatureJPQL(tempThreshold, start, end);
        return count != null ? count : 0L;
    }
}