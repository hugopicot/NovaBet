package com.polymarket.app.services;

import com.polymarket.infrastructure.StripeConfig;
import com.stripe.exception.StripeException;
import com.stripe.model.identity.VerificationSession;
import com.stripe.param.identity.VerificationSessionCreateParams;

import java.util.HashMap;
import java.util.Map;

public class StripeIdentityService {

    public record VerificationHandle(String sessionId, String hostedUrl) {}

    public VerificationHandle createVerification(long userId) throws StripeException {
        StripeConfig.init();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("novabet_user_id", String.valueOf(userId));

        VerificationSessionCreateParams params = VerificationSessionCreateParams.builder()
                .setType(VerificationSessionCreateParams.Type.DOCUMENT)
                .putAllMetadata(metadata)
                .setReturnUrl(StripeConfig.getReturnUrl())
                .build();

        VerificationSession session = VerificationSession.create(params);
        return new VerificationHandle(session.getId(), session.getUrl());
    }

    public String fetchStatus(String sessionId) throws StripeException {
        StripeConfig.init();
        VerificationSession session = VerificationSession.retrieve(sessionId);
        return session.getStatus();
    }
}
