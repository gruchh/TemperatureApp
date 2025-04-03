package com.example.TemperatureApp.repository;

import com.example.TemperatureApp.model.SensorAverageProjection;
import com.example.TemperatureApp.model.TemperatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TemperatureEntityRepository extends JpaRepository<TemperatureEntity, Long> {

    @Query("SELECT AVG(t.temperature) FROM TemperatureEntity t WHERE t.sensorId = :sensorId")
    Double findAverageTemperatureBySensorId(@Param("sensorId") String sensorId);

    @Query("SELECT t.sensorId as sensorId, AVG(t.temperature) as averageTemperature FROM TemperatureEntity t GROUP BY t.sensorId HAVING AVG(t.temperature) > :threshold")
    List<SensorAverageProjection> findSensorsWithAverageTemperatureAboveProjection(@Param("threshold") double threshold);

    @Query("SELECT COUNT(t) FROM TemperatureEntity t WHERE t.temperature > :tempThreshold AND t.timestamp BETWEEN :startTime AND :endTime")
    Long countReadingsAboveTemperatureJPQL(
            @Param("tempThreshold") double tempThreshold,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}