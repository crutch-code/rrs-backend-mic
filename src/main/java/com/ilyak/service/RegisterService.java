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
    TokenGeneratorService generator;

    @Inject
    TokenGenerator tokenGenerator;



    //todo: вынести хост
    @SneakyThrows
    public String generateRegisterTemplate(String userOid){
        return IOUtils.toString(ClassLoaderUtils.findResourceAsStream(
                "html-templates/confirm.html"),
                Charset.forName("UTF-8")
                        ).replace("[[confirmationLink]]", "https://crutch-code.ru/api/reg/confirm?token="
                + generator.generate(
                        "auth", "register", userOid
                )
        );
    }
}
