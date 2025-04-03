package com.example.temperatureapp;

import com.example.TemperatureApp.model.TemperatureEntity;
import com.example.TemperatureApp.repository.TemperatureEntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class TemperatureConsumer {
    private static final Logger logger = Logger.getLogger(TemperatureConsumer.class.getName());
    private final TemperatureEntityRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "temperature-topic", groupId = "temperature-group")
    public void consume(String message) {
        try {
            logger.info("Odebrano wiadomość: " + message);
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            TemperatureEntity reading = new TemperatureEntity();
            reading.setSensorId((String) data.get("sensorId"));
            reading.setTemperature(Double.parseDouble(data.get("temperature").toString()));
            reading.setTimestamp(LocalDateTime.now());
            repository.save(reading);
            logger.info("Zapisano do bazy: " + reading.getSensorId() + ", " + reading.getTemperature());
        } catch (IOException e) {
            logger.severe("Błąd deserializacji JSON: " + e.getMessage());
            throw new RuntimeException("Nie udało się przetworzyć wiadomości Kafka", e);
        } catch (NumberFormatException e) {
            logger.severe("Błąd parsowania temperatury: " + e.getMessage());
            throw new RuntimeException("Nieprawidłowy format temperatury", e);
        } catch (Exception e) {
            logger.severe("Nieoczekiwany błąd: " + e.getMessage());
            throw new RuntimeException("Błąd w konsumencie Kafka", e);
        }
    }
}