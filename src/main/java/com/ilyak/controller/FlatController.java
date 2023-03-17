package com.ilyak.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.Flat;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller("/api/flat")
@Tag(name = "Контроллер квартир арендодателя",
        description = "Данный котроллер отвечает за добавление и удаление квартир из списка пользователя"
)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class FlatController extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Добавление квартиры")
    @Post(uri = "/add", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Default.class)
    public HttpResponse<DefaultAppResponse> addFlat(@Part @Parameter(name="Flats array") Publisher<Flat> flats){
        try{

            return (HttpResponse<DefaultAppResponse>) Flowable.fromPublisher(flats).observeOn(Schedulers.io()).map(it -> {
                it.setFlatOwner(getCurrentUser());
                flatRepository.save(it);
                return HttpResponse.ok(responseService.success("Flat added successfully"));
            }).first(HttpResponse.serverError(responseService.error("Error")));

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Удаление квратиры")
    @Delete(uri = "/delete{?oid}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<DefaultAppResponse> deleteFlat(@QueryValue @Parameter(name = "Flat id") Optional<String> oid){
        try{
            flatRepository.deleteById(oid.orElseThrow());
            return HttpResponse.ok(responseService.success("Flat successful deleted"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение списка кваритр пользователя")
    @Get(uri = "/get{?page_num,page_size}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<Page<Flat>> getFlats(
            @QueryValue @Parameter(name = "Номер страницы") @Nullable Integer page_num,
            @QueryValue @Parameter(name = "Размер страницы") @Nullable Integer page_size
    ){
        try{
            return HttpResponse.ok(
                    flatRepository.findByFlatOwnerOid(
                            getCurrentUser().getOid(),
                            Pageable.from(getPageable(page_num, page_size))
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

}
