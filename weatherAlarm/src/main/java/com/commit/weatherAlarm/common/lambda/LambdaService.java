package com.commit.weatherAlarm.common.lambda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.nio.charset.StandardCharsets;

@Service
public class LambdaService {

    private final LambdaClient lambdaClient;

    @Autowired
    public LambdaService(LambdaClient lambdaClient) {
        this.lambdaClient = lambdaClient;
    }

    public String invokeLambdaFunction(String functionName, String payload) {
        try {
            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);
            return StandardCharsets.UTF_8.decode(invokeResponse.payload().asByteBuffer()).toString();
        } catch (LambdaException e) {
            e.printStackTrace();
            return "Lambda invocation failed: " + e.getMessage();
        }
    }
}
