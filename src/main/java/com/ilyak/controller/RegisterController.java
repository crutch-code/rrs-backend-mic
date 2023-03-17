package com.ilyak.controller;


import com.ilyak.entity.jpa.User;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.RegisterService;
import com.nimbusds.jwt.JWTParser;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Controller("/api/reg")
@Tag(name = "Контроллер Регистрации",
        description = "Данный котроллер отвечает за регистрацию пользователей в систему Remote Rent System"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
public class RegisterController extends BaseController{

    @Inject
    RegisterService registerService;
    public static final Logger registerLog = LoggerFactory.getLogger(RegisterController.class);


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Эндпоинт для регистрации пользователя")
    @Post(uri = "/check/email",produces = MediaType.APPLICATION_JSON)
    public HttpResponse<DefaultAppResponse> checkEmail(
            @Body User credential
    ){
        try {
            credential.setUserRegDate(LocalDateTime.now(ZoneId.systemDefault()).toLocalDate());
            credential.setOid(transactionalRepository.genOid().orElseThrow());
            userRepository.save(credential);
            emailService.send(
                    credential.getUserEmail(),
                    "Подтверждение регистрации",
                    registerService.generateRegisterTemplate(credential.getOid()));
            return HttpResponse.ok(
                    responseService.success()
            ).status(201);
        } catch (Exception e) {
            registerLog.error(e.getMessage());
            throw new InternalExceptionResponse("Error: " +e.getMessage() , responseService.error("error: " +e.getMessage()));
        }

    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Эндпоинт для регистрации пользователя")
    @Post(uri = "/register",produces = MediaType.APPLICATION_JSON)
    public HttpResponse<DefaultAppResponse> register(
            @Body User credential
    ){
        try {
            credential.setUserRegDate(LocalDateTime.now(ZoneId.systemDefault()).toLocalDate());
            credential.setOid(transactionalRepository.genOid().orElseThrow());
            userRepository.save(credential);
            emailService.send(
                    credential.getUserEmail(),
                    "Подтверждение регистрации",
                    registerService.generateRegisterTemplate(credential.getOid()));
            return HttpResponse.ok(
                    responseService.success()
              ).status(201);
        } catch (Exception e) {
            registerLog.error(e.getMessage());
            throw new InternalExceptionResponse("Error: " +e.getMessage() , responseService.error("error: " +e.getMessage()));
        }

    }

    @ExecuteOn(TaskExecutors.IO)
    @View("confirm-result")
    @Operation(summary = "Эндпоинт для регистрации пользователя")
    @Get(uri = "/confirm{?token}",produces = MediaType.TEXT_HTML)
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)
    public HttpResponse confirm(
            @QueryValue Optional<String> token
    ){
        try {
            if (!registerService.valid(JWTParser.parse(token.orElseThrow())) ){
                return HttpResponse.ok(
                    CollectionUtils.mapOf("success", false)
                );
            }
            userRepository.updateUserIsConfirmByOid(JWTParser.parse(token.orElseThrow()).getJWTClaimsSet().getStringClaim("oid"), true);

            return HttpResponse.ok(
                    CollectionUtils.mapOf("success", true)
            );

        } catch (Exception e) {
            registerLog.error(e.getMessage());
            throw new InternalExceptionResponse("Error: " +e.getMessage() , responseService.error("error: " +e.getMessage()));
        }

    }
}
