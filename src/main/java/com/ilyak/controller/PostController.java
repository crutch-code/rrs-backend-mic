package com.ilyak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.Post;
import com.ilyak.entity.jpa.RentOffer;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.requests.security.TokenRequest;
import com.ilyak.entity.responses.AppResponseWithObject;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.RentOfferRepository;
import com.ilyak.service.TokenGeneratorService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.validation.Validated;
import io.micronaut.websocket.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// todo: delete post

@Controller("/api/posts")
@Tag(name = "Контроллер взаимодействия с объявлениями",
        description = "Данный котроллер отвечает за взаимодействие с постами размещёнными в агрегаторе"
)
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class PostController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(PostController.class);


    @Inject
    RentOfferRepository offerRepository;

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение списка предложений аренды")
    @Get(uri = "/get", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView({JsonViewCollector.Post.BasicView.class} )
    public HttpResponse<Page<Post>> getPosts(
            @QueryValue @Nullable String locate,
            @QueryValue @Nullable Boolean by_user,
            @QueryValue @Nullable Sort.Order.Direction dir,
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        try{
            return HttpResponse.ok(postRepository.getFiltered(
                    locate == null? "" : locate,
                    "active",
                    (by_user != null && by_user)? getUserId() : null,
                    Sort.of(new Sort.Order("post_creation_date", dir, false)),
                    getPageable(page_num, page_size)
            ));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    //todo: moderators
    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Создать пост")
    @io.micronaut.http.annotation.Post(
            uri = "/create", produces = MediaType.APPLICATION_JSON_STREAM
    )
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecuredAnnotationRule.IS_ANONYMOUS)

    public HttpResponse<DefaultAppResponse> create(
            @Body Post post
    ){
        try{
            post.setOid(transactionalRepository.genOid().orElseThrow());
            post.setPostCreator(getCurrentUser());
            post.setPostStatus("active");
            post.setPostCreationDate(LocalDateTime.now(ZoneId.systemDefault()));
            postRepository.save(post);
            return HttpResponse.ok(responseService.success("Пост успешно создан"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Поместить пост в архив")
    @Get(uri = "/archive{?oid}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<DefaultAppResponse> archive(
            @QueryValue Optional<String> oid
    ){
        try{
            Post target = postRepository.findById(oid.orElseThrow()).orElseThrow();
            target.setPostStatus("archive");
            postRepository.update(target);
            return HttpResponse.ok(responseService.success("Пост успешно помещён в архив"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Обновить пост")
    @io.micronaut.http.annotation.Post(uri = "/update", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<DefaultAppResponse> update(
            @QueryValue Optional<String> oid
    ){
        try{
            return  HttpResponse.ok(responseService.toBeImplemented());
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Полчить забронированные даты")
    @Get(uri = "/reserved{?oid,start,end}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.RentOffer.OnlyDates.class)
    public HttpResponse<Page<RentOffer>> getReserved(
            @QueryValue Optional<String> oid,
            @QueryValue @Nullable String start,
            @QueryValue @Nullable String end
    ){
        try{
            return HttpResponse.ok(
                    offerRepository.reservedDates(
                            oid.orElseThrow(),
                            LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss")),
                            LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss")),
                            getPageable(0, 100)
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }


}
