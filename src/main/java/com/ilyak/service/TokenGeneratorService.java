package com.ilyak.service;

import com.nimbusds.jwt.JWTParser;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.security.token.generator.TokenGenerator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Singleton
public class TokenGeneratorService {

    @Inject
    TokenGenerator generator;

    public String generate(String resource, String type, String uid){
        return (String) generator.generateToken(
                CollectionUtils.mapOf(
                        "resource", resource,
                        "uid", uid,
                        "creation", LocalDateTime.now(ZoneId.systemDefault()).toString(),
                        "expired", LocalDateTime.now(ZoneId.systemDefault()).plusDays(1).toString()
                )
        ).orElseThrow();
    }

    @SneakyThrows
    public Boolean valid(String token){
        return LocalDateTime
                .parse(
                        JWTParser.parse(token)
                                .getJWTClaimsSet()
                                .getStringClaim("expired")
                )
                .isAfter(LocalDateTime.now(ZoneId.systemDefault()));
    }

    public enum Resource{
        AUTH("auth"),
        PUSH("push");
        private final String title;

        Resource(String frequency) {
            this.title =frequency;
        }

        public String getTitle() {
            return title;
        }

        public static Resource getInstance(String target){
            return Arrays.stream(Resource.values()).filter(p -> p.title.equals(target)).findFirst().orElse(null);
        }
    }
}
