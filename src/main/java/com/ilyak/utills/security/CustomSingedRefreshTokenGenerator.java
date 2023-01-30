package com.ilyak.utills.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.generator.RefreshTokenGenerator;
import io.micronaut.security.token.jwt.generator.RefreshTokenConfiguration;
import io.micronaut.security.token.jwt.generator.SignedRefreshTokenGenerator;
import io.micronaut.security.token.validator.RefreshTokenValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Singleton
@Replaces(SignedRefreshTokenGenerator.class)
public class CustomSingedRefreshTokenGenerator implements RefreshTokenGenerator, RefreshTokenValidator {
    private static final Logger LOG = LoggerFactory.getLogger(SignedRefreshTokenGenerator.class);
    private final JWSAlgorithm algorithm;
    private final JWSVerifier verifier;
    private final JWSSigner signer;

    @Inject
    private CustomJWTClaimsSetGenerator generator;


    public CustomSingedRefreshTokenGenerator(RefreshTokenConfiguration config) {
        byte[] secret = config.isBase64() ? Base64.getDecoder().decode(config.getSecret()) : config.getSecret().getBytes(UTF_8);
        this.algorithm = config.getJwsAlgorithm();
        try {
            this.signer = new MACSigner(secret);
        } catch (JOSEException e) {
            throw new ConfigurationException("unable to create a signer", e);
        }
        try {
            this.verifier = new MACVerifier(secret);
        } catch (JOSEException e) {
            throw new ConfigurationException("unable to create a verifier", e);
        }
    }

    @NonNull
    @Override
    public String createKey(@NonNull Authentication authentication) {
        return UUID.randomUUID().toString();
    }

    @SneakyThrows
    @NonNull
    @Override
    public Optional<String> generate(@NonNull Authentication authentication, @NonNull String token) {
        try {
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            generator.populateWithAuthentication(builder, authentication);
            JWSObject jwsObject = new SignedJWT(new JWSHeader(algorithm), builder.build());
            jwsObject.sign(signer);
            return Optional.of(jwsObject.serialize());
        } catch (JOSEException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("JOSEException signing a JWS Object");
            }
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public Optional<String> validate(@NonNull String refreshToken) {
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(refreshToken);
            if (jwsObject.verify(verifier)) {
                return Optional.of(jwsObject.getPayload().toString());
            }
        } catch (ParseException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Parse exception parsing refresh token {} into JWS Object", refreshToken);
            }
            return Optional.empty();
        } catch (JOSEException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("JOSEException parsing refresh token {} into JWS Object", refreshToken);
            }
        }
        return Optional.empty();
    }
}
