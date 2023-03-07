package com.ilyak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.Files;
import com.ilyak.entity.User;
import com.ilyak.entity.jsonviews.Default;
import com.ilyak.entity.requests.profile.ChangePasswordRequest;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.reactivex.rxjava3.core.Flowable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow;



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
    @JsonView(Default.class)
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
                return HttpResponse.ok(errorService.success("Success changed password"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), errorService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Password reset request")
    @Post(uri = "/password/reset/", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_ANONYMOUS)

    public HttpResponse<DefaultAppResponse> resetPassword(@Body ChangePasswordRequest request){
        try{
            return HttpResponse.ok(errorService.toBeImplemented());
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), errorService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Phone")
    @Patch(uri = "/phone/change{phone}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> changePhone(@QueryValue @NonNull String phone){
        try{
            User user = getCurrentUser();
            return HttpResponse.ok(errorService.success());
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), errorService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Adding user avatars")
    @Post(uri = "/avatar/add", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> addAvatar(@Part Publisher<CompletedFileUpload> avatars){
        try{
            User user = getCurrentUser();
            Flowable.fromPublisher(avatars).subscribe(emitted -> {
              Files target = filesService.save(emitted, filesService.getDirPattern() + filesService.getAvatars());
              filesRepository.save(target);
              user.getAvatars().add(target);

            });
            userRepository.update(user);
            return HttpResponse.ok(errorService.success("avatars uploaded successfully"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), errorService.error(ex.getMessage()));
        }
    }



}
