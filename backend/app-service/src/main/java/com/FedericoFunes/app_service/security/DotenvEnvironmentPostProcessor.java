package com.FedericoFunes.app_service.security;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                .directory("C:/Users/funes/Desktop/Bloodo.net/backend/app-service") // ruta al .env
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                environment.getSystemProperties().put(entry.getKey(), entry.getValue())
        );
    }
}
