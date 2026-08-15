package com.ProyectoFinal;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class StorageConfig {

    private final String rutaCredenciales;

    public StorageConfig(
            @Value("${firebase.json.path}") String jsonPath,
            @Value("${firebase.json.file}") String jsonFile) {
        this.rutaCredenciales = jsonPath + "/" + jsonFile;
    }

    @Bean
    public Storage storage() throws IOException {
        ClassPathResource recurso = new ClassPathResource(rutaCredenciales);
        try (InputStream entrada = recurso.getInputStream()) {
            GoogleCredentials credenciales = GoogleCredentials.fromStream(entrada);
            return StorageOptions.newBuilder()
                    .setCredentials(credenciales)
                    .build()
                    .getService();
        }
    }
}
