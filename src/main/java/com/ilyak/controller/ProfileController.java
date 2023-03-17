package com.ilyak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.requests.profile.ChangePasswordRequest;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
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
    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Infos about authored user")
    @Get(uri = "/infos", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @JsonView(JsonViewCollector.User.WithAvatarsList.class)
    public User userLogged(){
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





}
