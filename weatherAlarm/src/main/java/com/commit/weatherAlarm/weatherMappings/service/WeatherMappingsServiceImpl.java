package com.commit.weatherAlarm.weatherMappings.service;

import com.commit.weatherAlarm.common.lambda.LambdaService;
import com.commit.weatherAlarm.weatherApi.service.WeatherApiService;
import com.commit.weatherAlarm.weatherApi.view.WeatherInfoView;
import com.commit.weatherAlarm.weatherMappings.view.KeyView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class WeatherMappingsServiceImpl implements WeatherMappingsService {

    private S3Client s3Client;
    private WeatherApiService weatherApiService;
    private LambdaService lambdaService;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    public WeatherMappingsServiceImpl(S3Client s3Client, WeatherApiService weatherApiService, LambdaService lambdaService) {
        this.s3Client = s3Client;
        this.weatherApiService = weatherApiService;
        this.lambdaService = lambdaService;
    }

    @Override
    public KeyView getKeyByEmail(String email) throws IOException {
        ListObjectsV2Request listObjectsReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjResponse = s3Client.listObjectsV2(listObjectsReq);
        for (S3Object s3Object : listObjResponse.contents()) {
            if (!s3Object.key().endsWith(".json")) {
                log.info("Skipping non-JSON file: " + s3Object.key());
                continue;
            }
            JsonNode jsonNode = downloadJsonfile(s3Object.key());
            if (findEmailInJson(jsonNode, email)) {
                KeyView keyView = new KeyView();
                keyView.setKey(s3Object.key());
                return keyView;
            }
        }
        return null;
    }

    @Override
    public void setUserInfo(String key, Map<String, Object> jsonData) throws IOException {
        File jsonFile = convertObjectToJsonFile(jsonData);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(jsonFile));
    }

    @Override
    public void setAlarmInfo(String key, Map<String, Object> updates) throws IOException {
        Map<String, Object> existingData = downloadJson(key);
        existingData.putAll(updates);
        setUserInfo(key, existingData);
    }

    @Override
    public void deleteUserInfo(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // JSON 파일에서 time 값을 찾는 함수
    private String findTimeInJson(JsonNode jsonNode) {
        if (jsonNode.has("alarmTime")) {
            return jsonNode.get("alarmTime").asText();
        }
        if (jsonNode.isObject() || jsonNode.isArray()) {
            Iterator<JsonNode> elements = jsonNode.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                String timeValue = findTimeInJson(element);
                if (timeValue != null) {
                    return timeValue;
                }
            }
        }
        return null;
    }

    private void getWeatherInfo(String cityCode) throws IOException {
        //날씨 api 통해서 날씨 가져와서 아래에 넣기
        Mono<WeatherInfoView> weatherInfoMono = weatherApiService.getWeather(cityCode);
        WeatherInfoView weatherInfo = weatherInfoMono.block();
        System.out.println(weatherInfo);
    }

    public String getEmailFromJson(String key) throws IOException {
        JsonNode jsonNode = downloadJsonfile(key);
        return jsonNode.get("email").asText(); // email 값을 직접 반환
    }

    public String getCityCodeFromJson(String key) throws IOException {
        JsonNode jsonNode = downloadJsonfile(key);
        return jsonNode.get("cityCode").asText(); // cityCode 값을 직접 반환
    }

    private String createPayload(WeatherInfoView weatherInfo, String email) {
        return "{\"날씨\":\"" + weatherInfo.getWeather() + "\"}";
    }

    // 모든 파일을 다운로드하여 time값이 현재시간과 일치하면 그 파일의 키 값을 저장
    @Scheduled(cron = "0 * * * * *")
    private void checkTimeValues() throws IOException {
        ListObjectsV2Request listObjectsReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjResponse = s3Client.listObjectsV2(listObjectsReq);
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        for (S3Object s3Object : listObjResponse.contents()) {
            try {
                if (!s3Object.key().endsWith(".json")) {
                    log.info("Skipping non-JSON file: " + s3Object.key());
                    continue;
                }
                JsonNode jsonNode = downloadJsonfile(s3Object.key());
                String timeValue = findTimeInJson(jsonNode);
                // 현재 시간과 유저가 설정한 알람시간이 똑같을 때 실행
                if (currentTime.equals(timeValue)) {
                    String email = getEmailFromJson(s3Object.key());
                    String cityCode = getCityCodeFromJson(s3Object.key());
                    WeatherInfoView weatherInfo = weatherApiService.getWeather(cityCode).block();

                    String payload = "{" +
                            "\"날씨\":\"" + weatherInfo.getWeather() + "\"," +
                            "\"온도\":\"" + weatherInfo.getTemp() + "\"," +
                            "\"지역\":\"" + weatherInfo.getCityName() + "\"," +
                            "\"이메일\":\"" + email + "\"" +
                            "}";

                    String lambdaResponse = lambdaService.invokeLambdaFunction(
                            "weather_alarm_test", payload);
                }
            } catch (IOException e) {
                log.error("Error processing file: " + s3Object.key(), e);
            }
        }
    }


    public Map<String, Object> downloadJson(String key) throws IOException {
        File tempFile = File.createTempFile("temp", ".json");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.getObject(getObjectRequest, Paths.get(tempFile.getAbsolutePath()));
        return convertJsonFileToObject(tempFile);
    }

    private Map<String, Object> convertJsonFileToObject(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, Map.class);
    }

    private File convertObjectToJsonFile(Object data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = File.createTempFile("temp", ".json");
        try (FileOutputStream fos = new FileOutputStream(jsonFile)) {
            objectMapper.writeValue(fos, data);
        }
        return jsonFile;
    }

    private JsonNode downloadJsonfile(String key) throws IOException {
        // 고유한 임시 파일 이름 생성
        String tempFileName = "temp-" + UUID.randomUUID() + ".json";
        File tempFile = new File(System.getProperty("java.io.tmpdir"), tempFileName);

        // 기존 파일이 존재하면 삭제
        if (tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                throw new IOException("Failed to delete existing temp file: " + tempFile.getAbsolutePath());
            }
        }

        // 임시 파일이 삭제되도록 설정
        tempFile.deleteOnExit();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(tempFile.getAbsolutePath()));

        return parseJsonFile(tempFile);
    }

    private boolean findEmailInJson(JsonNode jsonNode, String email) {
        if (jsonNode.isObject() || jsonNode.isArray()) {
            Iterator<JsonNode> elements = jsonNode.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                if (findEmailInJson(element, email)) {
                    return true;
                }
            }
        } else if (jsonNode.isTextual() && jsonNode.asText().equals(email)) {
            return true;
        }
        return false;
    }

    private JsonNode parseJsonFile(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(file);
    }
}
