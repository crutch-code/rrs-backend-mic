package com.ilyak.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.MultipartContent;
import com.ilyak.entity.Files;
import com.ilyak.entity.User;
import com.ilyak.entity.jsonviews.Default;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Type;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.CompletedPart;
import io.micronaut.http.multipart.PartData;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;

@Controller("/api/reg")
@Tag(name = "Контроллер Регистрации",
        description = "Данный котроллер отвечает за регистрацию пользователей в систему Remote Rent System"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
public class RegisterController extends BaseController{

    public static final Logger registerLog = LoggerFactory.getLogger(RegisterController.class);


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "TRy email")
    @Get(value = "/try", produces = MediaType.APPLICATION_JSON)
    public DefaultAppResponse trysend(){
        emailService.send();
        return new DefaultAppResponse();
    }


//    @ExecuteOn(TaskExecutors.IO)
//    @Operation(summary = "Эндпоинт для подтверждения ползователя, через почту")
//    @Get(uri="/confirm")
//    public HttpResponse<DefaultAppResponse> confirm(){
//
//    }



    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Эндпоинт для регистрации пользователя")
    @Post(uri = "/register",produces = MediaType.APPLICATION_JSON)
    public HttpResponse<DefaultAppResponse> register(
            @Body User credential
    ){
        try {
            credential.setUserRegDate(new Date(System.currentTimeMillis()));
            credential.setOid(transactionalRepository.genOid().orElseThrow());
            userRepository.save(credential);
            return HttpResponse.ok(
                    errorService.success()
              ).status(201);
        } catch (Exception e) {
            registerLog.error(e.getMessage());
            throw new InternalExceptionResponse("Error: " +e.getMessage() , errorService.error("error: " +e.getMessage()));
        }

    }
}
