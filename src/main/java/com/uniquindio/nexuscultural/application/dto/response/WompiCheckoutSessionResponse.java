package com.uniquindio.nexuscultural.application.dto.response;

import java.util.UUID;

public record WompiCheckoutSessionResponse(
        String checkoutUrl,
        String reference,
        String publicKey,
        long amountInCents,
        String currency,
        String integritySignature,
        String redirectUrl,
        UUID pedidoId
) {
}
