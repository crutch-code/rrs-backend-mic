package com.ilyak.controller;

import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Controller("/api/admin")
@Secured(SecurityRule.IS_AUTHENTICATED)
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
@SecurityRequirement(name = "BearerAuth")
public class AdminController extends BaseController{

//    @Error(exception = Exception.class, global = true)
//    public HttpResponse<DefaultAppResponse> runtimeExceptionResponseHttpResponse(HttpRequest request, Exception ex){
//        if (ex instanceof InternalExceptionResponse)
//            return HttpResponse.serverError(((InternalExceptionResponse) ex).getResponse());
//
//        return HttpResponse.serverError(
//                new DefaultAppResponse(
//                        100,
//                        "Unexpected Internal error: " +  ex.getMessage(),
//                        request.getPath())
//        );
//    }

}
