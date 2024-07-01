package com.commit.weatherAlarm.weatherApi.service;

import com.commit.weatherAlarm.weatherApi.view.WeatherInfoView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherApiServiceImpl implements WeatherApiService {

    private final String apiKey = "1b63b6cc88cd0a3b9c0b68934ddca166";
    private final WebClient webClient;

    public WeatherApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://api.openweathermap.org").build();
    }

    @Override
    public Mono<WeatherInfoView> getWeather(String cityCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/data/2.5/weather")
                        .queryParam("id", cityCode)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric") // 온도를 섭씨로 받기 위해 units 파라미터 추가
                        .queryParam("lang", "kr")
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

                        String description = weatherNode.path("description").asText(); // 날씨 설명
                        double temp = mainNode.path("temp").asDouble();
                        String cityName = root.path("name").asText();

                        WeatherInfoView weatherInfoView = new WeatherInfoView();
                        weatherInfoView.setWeather(description);
                        weatherInfoView.setCityName(cityName);
                        weatherInfoView.setTemp(temp);

                        return weatherInfoView;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }
}
