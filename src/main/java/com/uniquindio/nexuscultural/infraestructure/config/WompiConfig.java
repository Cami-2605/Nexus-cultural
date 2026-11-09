package com.uniquindio.nexuscultural.infraestructure.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class WompiConfig {

    @Value("${wompi.public-key:pub_test_DTOfb5kgRzTVcj02RTqIwT5zECp9F0mg}")
    private String publicKey;

    @Value("${wompi.private-key:prv_test_cT1zq6b0IxoV8GcmkKMi2Aj65Rlfp8CZ}")
    private String privateKey;

    @Value("${wompi.events-secret:test_events_P9G7jaCANcG5REdXbM3FRw1w9e2VcoLj}")
    private String eventsSecret;

    @Value("${wompi.integrity-secret:test_integrity_2sslyC0apcfIDQpXQgDMuSQYbkE2qTob}")
    private String integritySecret;

    @Value("${wompi.checkout-url:https://checkout.wompi.co/p/}")
    private String checkoutUrl;

    @Value("${wompi.api-url:https://sandbox.wompi.co/v1}")
    private String apiUrl;
}
