package com.uniquindio.nexuscultural.infraestructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppVersionController {

    @Value("${app.version.min:1.0.0}")
    private String minVersion;

    @Value("${app.version.latest:1.0.0}")
    private String latestVersion;

    @Value("${app.version.force-update:false}")
    private boolean forceUpdate;

    @Value("${app.version.update-url:https://drive.google.com/drive/folders/1Qz836tn3XpFgBuoKCzblpEpq7fXKgdrR?usp=drive_link}")
    private String updateUrl;

    @Value("${app.version.message:Nueva versión requerida de la aplicación móvil. Por favor actualiza la app para continuar.}")
    private String message;

    @GetMapping({"/app-version", "/version"})
    public ResponseEntity<Map<String, Object>> getAppVersion() {
        return ResponseEntity.ok(Map.of(
                "minVersion", minVersion,
                "latestVersion", latestVersion,
                "forceUpdate", forceUpdate,
                "updateUrl", updateUrl,
                "message", message
        ));
    }
}
