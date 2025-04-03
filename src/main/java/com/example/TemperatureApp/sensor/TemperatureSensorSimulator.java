package com.example.TemperatureApp.sensor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;

@Component
public class TemperatureSensorSimulator {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random random = new Random();

    public TemperatureSensorSimulator(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void generateTemperature() {
        String[] sensorIds = {"sensor1", "sensor2"};
        for (String sensorId : sensorIds) {
            double temperature = 15 + random.nextDouble() * 20;
            String message = String.format(Locale.US, "{\"sensorId\": \"%s\", \"temperature\": %.2f}", sensorId, temperature);
            kafkaTemplate.send("temperature-topic", message);
            System.out.println("Wysłano: " + message);
        }
    }
}