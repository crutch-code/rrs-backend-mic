package com.ilyak.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.User;
import com.ilyak.entity.jsonviews.Default;
import com.ilyak.entity.requests.RefreshTokenRequest;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.UserLogoutService;
import com.ilyak.utills.security.AuthProviderUser;
import com.ilyak.utills.security.RefreshTokenHandler;
import com.ilyak.utills.security.events.CustomLoginSuccessfulEvent;
import com.ilyak.utills.security.requests.CustomAuthRequest;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.*;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.validation.Validated;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;


@Controller("/api/auth")
@Tag(name = "Контроллер аутнетификации",
        description = "Данный котроллер отвечает за логгирование пользователей в систему Remote Rent System"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class AuthController extends BaseController {


    public static final Logger authLog = LoggerFactory.getLogger(AuthController.class);

    @Inject
    UserLogoutService logoutService;
    @Inject
    private AuthProviderUser providerUser;

    @Inject
    private ApplicationEventPublisher<io.micronaut.context.event.ApplicationEvent> eventPublisher;

    @Inject
    private RefreshTokenHandler refreshTokenHandler;

    @Inject
    private LoginHandler loginHandler;


    @Options(uri = "/login")
    public String handleOptionsLogin() {

        // let the cors filter do its job
        return "200";
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Login")
    @Post(uri = "/login", produces = MediaType.APPLICATION_JSON)
    @Schema(anyOf = {AccessRefreshToken.class})
    public Flowable<HttpResponse<?>> login(
            @Valid @Body CustomAuthRequest authRequest,
            HttpRequest<?> request
    ){
        Flowable<AuthenticationResponse> responseFlowable = Flowable.fromPublisher(providerUser.authenticate(request, authRequest));
        return responseFlowable.map(
                mapper -> {
                    if(!mapper.isAuthenticated()) {
                        authLog.info(authRequest.getIdentity().toString() +" isn't authenticated ");
                        eventPublisher.publishEvent(new LoginFailedEvent(mapper));
                        return loginHandler.loginFailed(mapper, request);
                    }
                    authLog.info(authRequest.getIdentity().toString() +" is authenticated ");
                    eventPublisher.publishEvent(new CustomLoginSuccessfulEvent(mapper.getAuthentication()));
                    return loginHandler.loginSuccess(mapper.getAuthentication().orElseThrow(), request);
                });
    }



    @SneakyThrows
    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Refresh authority token")
    @Post(uri = "/refresh", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Schema(anyOf = {AccessRefreshToken.class})
    public @NonNull Flowable<MutableHttpResponse<?>> refresh(@Valid @Body RefreshTokenRequest refresh, HttpRequest<?> request) {
//        if(getCurrentUser() == null)
//            throw new InternalExceptionResponse("That user already logged out", errorService.error("That user already logged out"));

        return Flowable.fromPublisher(
                    refreshTokenHandler.getAuthentication(refresh.getToken())
                ).map(
                        mapper-> loginHandler.loginRefresh(mapper, refresh.getToken(), request)
                );
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Infos about authored user")
    @Get(uri = "/infos", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @JsonView(Default.class)
    public User userLogged(){
        return getCurrentUser();
    }


    @SneakyThrows
    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Logout current user")
    @Post(uri = "/logout", produces = MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(Default.class)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public DefaultAppResponse logout(HttpRequest<?> request){
        try {
            logoutService.putLogout(request.getHeaders().getAuthorization().orElseThrow(), getCurrentUser());
            return errorService.success("Unauthorized");
        }catch (Exception ex){
            authLog.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), errorService.error(ex.getMessage()));
        }
    }
}
