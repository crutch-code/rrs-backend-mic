package com.ilyak.service;


import com.ilyak.entity.jpa.User;
import com.ilyak.utills.security.CustomJWTClaimsSetGenerator;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.views.ModelAndView;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.ClassLoaderUtils;

import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Singleton
public class RegisterService {

    @Inject
    TokenGenerator tokenGenerator;


    String generateToken(String oid){
        Map<String, Object> claims = new HashMap<>();
        claims.put("oid", oid);
        claims.put("creation", LocalDateTime.now(ZoneId.systemDefault()).toString());
        claims.put("expiresIn", 1); //день
        return tokenGenerator.generateToken(claims).orElseThrow();
    }

    @SneakyThrows
    public Boolean valid(JWT token){
        return  LocalDateTime
                .parse(token.getJWTClaimsSet().getStringClaim("creation"))
                .plusDays(token.getJWTClaimsSet().getIntegerClaim("expiresIn"))
                .isAfter(LocalDateTime.now(ZoneId.systemDefault()));
    }

    //todo: вынести хост
    @SneakyThrows
    public String generateRegisterTemplate(String userOid){
        return IOUtils.toString(ClassLoaderUtils.findResourceAsStream(
                "html-templates/confirm.html"),
                Charset.forName("UTF-8")
                        ).replace("[[confirmationLink]]", "crutch-code.ru/api/reg/confirm?token=" + generateToken(userOid)
        );
    }
}
