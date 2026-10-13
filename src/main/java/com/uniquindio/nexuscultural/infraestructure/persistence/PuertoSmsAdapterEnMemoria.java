package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.repository.PuertoSMS;
import org.springframework.stereotype.Component;

@Component
public class PuertoSmsAdapterEnMemoria implements PuertoSMS {

    @Override
    public void enviarMensajeSms(String telefono, String mensaje) {
        // Simulación de envío de SMS para pruebas locales en consola
        System.out.println("[SMS SIMULADO] Para: " + telefono + " | Mensaje: " + mensaje);
    }
}
