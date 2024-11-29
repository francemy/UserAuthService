package com.exemplo.usermanagement.logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EnvironmentVariablesLogger {

    @Value("${SPRING_DATASOURCE_URL}")
    private String datasourceUrl;

    @Value("${SPRING_DATASOURCE_USERNAME}")
    private String datasourceUsername;

    @Value("${SPRING_DATASOURCE_PASSWORD}")
    private String datasourcePassword;

    @PostConstruct
    public void logEnvironmentVariables() {
        System.out.println("SPRING_DATASOURCE_URL: " + datasourceUrl);
        System.out.println("SPRING_DATASOURCE_USERNAME: " + datasourceUsername);
        System.out.println("SPRING_DATASOURCE_PASSWORD: " + datasourcePassword);
    }
}
