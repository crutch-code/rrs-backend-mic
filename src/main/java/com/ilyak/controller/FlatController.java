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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Operation(summary = "Добавление объекта пользователя")
    @Post(uri = "/add", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<DefaultAppResponse> addFlat(@Body List<Flat> flats){
        try{
            flats.stream().map(m-> {
                m.setFlatOwner(getCurrentUser());
                m.setOid(transactionalRepository.genOid().orElseThrow());
                return m;
            }).collect(Collectors.toList());
            flatRepository.saveAll(flats);
            return HttpResponse.ok(responseService.success("квартиры добвалены"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Удаление объекта пользователя")
    @Delete(uri = "/delete{?oid}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<DefaultAppResponse> deleteFlat(@QueryValue Optional<String> oid){
        try{
            flatRepository.deleteById(oid.orElseThrow());
            return HttpResponse.ok(responseService.success("Flat successful deleted"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение списка объектов пользователя")
    @Get(uri = "/get{?page_num,page_size}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Flat.BasicView.class)
    public HttpResponse<Page<Flat>> getFlats(
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        try{
            return HttpResponse.ok(
                    flatRepository.findByFlatOwnerOid(
                            getCurrentUser().getOid(),
                            getPageable(page_num, page_size)
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

}
