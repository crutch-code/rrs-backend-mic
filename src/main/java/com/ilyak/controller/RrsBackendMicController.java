package com.ilyak.controller;

import com.ilyak.controller.BaseController;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.service.ErrorService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Controller("/rrsBackendMic")
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class RrsBackendMicController extends BaseController {

    @ExecuteOn(TaskExecutors.IO)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @SecurityRequirement(name = "BearerAuth")
    @Get(uri = "/try", produces = MediaType.APPLICATION_JSON)
    public DefaultAppResponse index() {
        return errorService.success();
    }
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get(uri = "/try2")
    public DefaultAppResponse index2() {
        return errorService.success();
    }
}