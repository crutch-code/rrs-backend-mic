package com.ilyak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.Contract;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.repository.ContractRepository;
import io.micronaut.data.model.Page;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
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
import jakarta.inject.Inject;

import java.util.Optional;

@Controller("/api/contracts")
@Tag(name = "Контроллер для получения списка контрактов пользователя",
        description = "Данный котроллер cодержит метод который позволяет получить список контрактов пользователя порционно"
)
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class ContractController  extends BaseController{

    @Inject
    ContractRepository repository;

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Infos about authored user")
    @Get(uri = "/get", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Contract.BasicView.class)
    public Page<Contract> getContracts(
            @QueryValue Optional<Integer> page_num,
            @QueryValue Optional<Integer> page_size
    ){
        return repository.findByUsers(getUserId(), getPageable(page_num.orElse(null), page_size.orElse(null)));
    }
}
