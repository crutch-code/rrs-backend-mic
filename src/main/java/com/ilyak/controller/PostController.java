package com.ilyak.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/profile")
@Tag(name = "Контроллер взаимодействия с объявлениями",
        description = "Данный котроллер отвечает за взаимодействие с постами размещёнными в агрегаторе"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class PostController extends BaseController{

}
