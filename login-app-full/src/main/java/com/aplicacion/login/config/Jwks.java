package com.aplicacion.login.config;

import java.util.UUID;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

/**
 * Utility class to generate RSA JWKs for the Authorization Server.
 */
public class Jwks {
    /**
     * Generates a new RSAKey for signing JWTs.
     * @return RSAKey containing public and private keys
     */
    public static RSAKey generateRsa() {
        try {
            return new RSAKeyGenerator(2048)
                    .keyUse(KeyUse.SIGNATURE)      // indicate the purpose of the key
                    .algorithm(JWSAlgorithm.RS256)  // JWT signing algorithm
                    .keyID(UUID.randomUUID().toString()) // unique identifier
                    .generate();
        } catch (JOSEException e) {
            throw new IllegalStateException("Error generating RSA key", e);
        }
    }
}
