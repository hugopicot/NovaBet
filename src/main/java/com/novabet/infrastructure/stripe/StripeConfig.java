package com.novabet.infrastructure.stripe;

import com.stripe.Stripe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StripeConfig {

    private static final String CONFIG_FILE = "/config.properties";
    private static boolean initialized = false;
    private static String returnUrl;

    public static synchronized void init() {
        if (initialized) return;

        Properties props = new Properties();
        try (InputStream in = StripeConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException("config.properties introuvable.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Lecture config.properties impossible", e);
        }

        String key = props.getProperty("stripe.secret_key", "").trim();
        if (key.isEmpty()) {
            throw new IllegalStateException(
                "stripe.secret_key vide dans config.properties — crée un compte Stripe et colle ta sk_test_..."
            );
        }
        Stripe.apiKey = key;
        returnUrl = props.getProperty("stripe.return_url", "https://example.com/novabet-return").trim();
        initialized = true;
    }

    public static String getReturnUrl() {
        init();
        return returnUrl;
    }
}
