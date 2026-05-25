package com.polymarket.app.services;

import com.polymarket.infrastructure.StripeConfig;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import java.util.HashMap;
import java.util.Map;

public class StripePaymentService {

    public record CheckoutHandle(String sessionId, String hostedUrl) {}

    public record CheckoutResult(String status, String paymentMethodType) {}

    public CheckoutHandle createCheckout(long userId, double amountUsd) throws StripeException {
        StripeConfig.init();

        long amountCents = Math.round(amountUsd * 100);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("novabet_user_id", String.valueOf(userId));

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(StripeConfig.getReturnUrl() + "?status=success&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(StripeConfig.getReturnUrl() + "?status=cancel")
                .putAllMetadata(metadata)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Dépôt NovaBet ($NVB)")
                                                                .setDescription("Crédit de " + amountUsd + " $NVB sur votre wallet")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return new CheckoutHandle(session.getId(), session.getUrl());
    }

    public CheckoutResult fetchStatus(String sessionId) throws StripeException {
        StripeConfig.init();
        SessionRetrieveParams params = SessionRetrieveParams.builder()
                .addExpand("payment_intent")
                .build();
        Session session = Session.retrieve(sessionId, params, null);
        String status = session.getPaymentStatus();
        String method = null;
        if (session.getPaymentIntentObject() != null
                && session.getPaymentIntentObject().getPaymentMethodTypes() != null
                && !session.getPaymentIntentObject().getPaymentMethodTypes().isEmpty()) {
            method = session.getPaymentIntentObject().getPaymentMethodTypes().get(0);
        }
        return new CheckoutResult(status, method);
    }
}
