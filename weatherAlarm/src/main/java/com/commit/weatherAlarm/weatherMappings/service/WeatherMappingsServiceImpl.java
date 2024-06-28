package com.commit.weatherAlarm.weatherMappings.service;

import com.commit.weatherAlarm.weatherMappings.view.KeyView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@Slf4j
@Service
public class WeatherMappingsServiceImpl implements WeatherMappingsService {

    private S3Client s3Client;
    private ModelMapper modelMapper;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    public WeatherMappingsServiceImpl(S3Client s3Client, ModelMapper modelMapper) {
        this.s3Client = s3Client;
        this.modelMapper = modelMapper;
    }

    public KeyView getKeyByEmail(String email) throws IOException {
        ListObjectsV2Request listObjectsReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjResponse = s3Client.listObjectsV2(listObjectsReq);
        for (S3Object s3Object : listObjResponse.contents()) {
            JsonNode jsonNode = downloadJsonfile(s3Object.key());
            if (findEmailInJson(jsonNode, email)) {
                KeyView keyView = new KeyView();
                keyView.setKey(s3Object.key());
                return keyView;
            }
        }
        return null;
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
