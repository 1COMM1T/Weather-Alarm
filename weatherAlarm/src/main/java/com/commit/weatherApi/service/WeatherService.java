package com.commit.weatherApi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private final String apiKey = "1b63b6cc88cd0a3b9c0b68934ddca166";
    private final WebClient webClient;

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://api.openweathermap.org").build();
    }

    public Mono<String> getWeather(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/data/2.5/weather")
                        .queryParam("id", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric") // 온도를 섭씨로 받기 위해 units 파라미터 추가
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        // JSON 파싱
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(response);
                        JsonNode weatherNode = root.path("weather").get(0);
                        JsonNode mainNode = root.path("main");

                        String main = weatherNode.path("main").asText();
                        String icon = weatherNode.path("icon").asText();
                        double temp = mainNode.path("temp").asDouble();
                        String cityName = root.path("name").asText();

                        String iconUrl = String.format("http://openweathermap.org/img/wn/%s.png", icon);

                        return String.format("Weather: %s, Location: %s, Temp: %.2f°C, Icon: %s",
                                main, cityName, temp, iconUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Error parsing weather data";
                    }
                });
    }
}

