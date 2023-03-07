package com.ilyak.utills.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.User;
import com.ilyak.service.UserLogoutService;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.RolesFinder;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.validator.DefaultJwtAuthenticationFactory;
import io.micronaut.security.token.jwt.validator.JwtAuthenticationFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@Singleton
@Replaces(value = DefaultJwtAuthenticationFactory.class)
public class CustomJwtAuthFactory implements JwtAuthenticationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJwtAuthenticationFactory.class);

    @Inject
    private UserLogoutService logoutService;
    private final TokenConfiguration tokenConfiguration;
    private final RolesFinder rolesFinder;

    public CustomJwtAuthFactory(TokenConfiguration tokenConfiguration, RolesFinder rolesFinder) {
        this.tokenConfiguration = tokenConfiguration;
        this.rolesFinder = rolesFinder;
    }

    @Override
    public Optional<Authentication> createAuthentication(JWT token) {
        try {
            final JWTClaimsSet claimSet = token.getJWTClaimsSet();
            if (claimSet == null || logoutService.isLogout(claimSet.getStringClaim("session"))) {
                return Optional.empty();
            }
            Map<String, Object> attributes = claimSet.getClaims();
            return userForClaims(claimSet).map(mapper ->
                new CustomAuthentication(
                            userForClaims(claimSet).orElseThrow(),
                            rolesFinder.resolveRoles(attributes),
                            attributes,
                            sessionUuidForClaims(claimSet).orElseThrow()
                        )
            );
        } catch (ParseException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("ParseException creating authentication", e);
            }
        }
        return Optional.empty();
    }

//    protected Optional<String> usernameForClaims(JWTClaimsSet claimSet) throws ParseException {
//        String username = claimSet.getStringClaim(tokenConfiguration.getNameKey());
//        if (username == null) {
//            return Optional.ofNullable(claimSet.getSubject());
//        }
//        return Optional.of(username);
//    }

    @SneakyThrows
    protected Optional<User> userForClaims(JWTClaimsSet claimsSet){
        LOG.info("claims set: " + claimsSet.getClaims().toString());
        return  Optional.of(new ObjectMapper().registerModule(new JavaTimeModule()).readValue(claimsSet.getClaim("credentials").toString(), User.class));
    }

    @SneakyThrows
    protected Optional<String> sessionUuidForClaims(JWTClaimsSet claimsSet){
        return Optional.of(claimsSet.getStringClaim("session"));
    }
}
