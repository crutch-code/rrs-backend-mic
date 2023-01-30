package com.ilyak.controller;


import io.micronaut.http.annotation.Controller;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/reg")
@Tag(name = "Контроллер Регистрации",
        description = "Данный котроллер отвечает за регистрацию пользователей в систему Remote Rent System"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
@Validated
public class RegisterController extends BaseController{


}
