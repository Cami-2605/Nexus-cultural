package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FirebaseNotificadorAdapter {

    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificadorAdapter.class);

    public void enviarCorreoOtp(String emailDestino, String codigoOtp) {
        try {
            // 1. Obtener la instancia específica de tu base de datos Firestore personalizada
            //Firestore db = FirestoreClient.getFirestore("db-nexus-cultural-firebase");
            Firestore db = FirestoreClient.getFirestore("(default)");

            // 2. Construir la estructura exacta que requiere la extensión Trigger Email
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("to", emailDestino);

            Map<String, Object> message = new HashMap<>();
            message.put("subject", "Tu código OTP - Nexus Cultural");
            message.put("text", "Hola,\n\nTu código de verificación para ingresar a Nexus Cultural es: "
                    + codigoOtp + "\n\nEste código vence en 5 minutos.");

            emailData.put("message", message);

            // 3. Insertar el registro en la colección "mail"
            ApiFuture<DocumentReference> future = db.collection("mail").add(emailData);

            log.info("Documento de correo OTP registrado en Firestore con ID: {}", future.get().getId());

        } catch (Exception e) {
            log.error("Error al registrar el correo OTP en Firestore: {}", e.getMessage());
        }
    }
}