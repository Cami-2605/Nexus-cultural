package com.uniquindio.nexuscultural.infraestructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    // Inyecta el archivo desde el classpath definido en application.properties
    @Value("${firebase.config.path:classpath:firebase-key-nexus-cultural-sevicio-correo.json}")
    private Resource firebaseResource;

    @PostConstruct
    public void inicializarFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Lee el flujo directamente desde los recursos del proyecto/JAR
                try (InputStream serviceAccount = firebaseResource.getInputStream()) {

                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar Firebase Admin SDK desde resources: " + e.getMessage(), e);
        }
    }
}