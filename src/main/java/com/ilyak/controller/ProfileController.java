package com.ilyak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.Rating;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.requests.profile.ChangePasswordRequest;
import com.ilyak.entity.responses.AppResponseWithObject;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;



/*
* TODO:
*   1. Pass hash
*   2. Reset confirm via email, phone?
*   3. Confirm phone
*
* */

@Controller("/api/profile")
@Tag(name = "Изменение профиля",
        description = "Данный котроллер отвечает за взаимодействие с профилем пользователя"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class ProfileController extends BaseController{
    public static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Infos about authored user")
    @Get(uri = "/infos", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @JsonView(JsonViewCollector.User.WithAvatarsList.class)
    public User userLogged(
            @QueryValue Optional<String> oid
    ){
        if (oid.isPresent()){
            return userRepository.findById(oid.get()).orElseThrow(()-> new RuntimeException("Пользователь с таким oid не найден"));
        }
        return getCurrentUser();
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Change password with old password")
    @Post(uri = "/password/change", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> changePassword(@Body ChangePasswordRequest request){
        try{
                User user = getCurrentUser();
                if (!request.getOldPassword().equals(user.getUserPassword()))
                    return HttpResponse.status(HttpStatus.FORBIDDEN).body(new DefaultAppResponse());
                user.setUserPassword(request.getNewPassword());
                userRepository.update(user);
                return HttpResponse.ok(responseService.success("Success changed password"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Password reset request")
    @Post(uri = "/password/reset/", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_ANONYMOUS)

    public HttpResponse<DefaultAppResponse> resetPassword(@Body ChangePasswordRequest request){
        try{
            return HttpResponse.ok(responseService.toBeImplemented());
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Phone")
    @Patch(uri = "/phone/change{?phone}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> changePhone(@QueryValue Optional<String> phone){
        try{
            User user = getCurrentUser();
            user.setUserPhoneNumber(phone.orElseThrow());
            userRepository.update(user);
            return HttpResponse.ok(responseService.success("phone has been changed"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Phone")
    @Patch(uri = "/contact/{type}/change{?link}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> changeTelegramLink(
            @QueryValue Optional<String> link,
            @PathVariable String type
    ){
        try{
            User user = getCurrentUser();
            switch (type){
                case "telegram" -> user.setTelegramLink(link.orElseThrow());
                case "whats_up" -> user.setWhatsUpLink(link.orElseThrow());
                default -> throw new RuntimeException("Не верный тип контакта");
            }
            userRepository.update(user);
            return HttpResponse.ok(responseService.success("Ссылка типа: " + type + "установлена"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Изменение ФИО пользователя")
    @Patch(uri = "/full_name/change{?name}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> changeName(
            @QueryValue Optional<String> name
    ){
        try{
            User user = getCurrentUser();
            user.setUserName(name.orElseThrow(()-> new RuntimeException("Не передан параметр имени")));
            userRepository.update(user);
            return HttpResponse.ok(responseService.success("Имя пользователя изменено на: " + name.get()));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Генерация отчёта для пользователя")
    @Get(uri = "/report", produces = MediaType.APPLICATION_PDF)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public StreamedFile getReport(){
        try{
            Map<String, Object> anchors = transactionalRepository.getReport(getUserId());
            anchors.put("real_date_time", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            return new StreamedFile(
                    new ByteArrayInputStream(
                            documentsService.genReport(anchors).toByteArray()
                    ),
                    MediaType.APPLICATION_PDF_TYPE
            ).attach("report.pdf");
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Phone")
    @Patch(uri = "/rating/update{?score,target_oid}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> addRating(
            @QueryValue Optional<BigDecimal> score,
            @QueryValue Optional<String> target_oid

    ){
        try{
            ratingRepository.save(
                    new Rating(
                            transactionalRepository.genOid().orElseThrow(),
                            userRepository.findById(target_oid.orElseThrow()).orElseThrow(),
                            score.orElseThrow()
                    )
            );
            return HttpResponse.ok(responseService.success("рейтинг пользователя добавлен"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }





}
