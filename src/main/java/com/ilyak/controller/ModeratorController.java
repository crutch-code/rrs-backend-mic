package com.ilyak.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.Post;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.AppResponseWithObject;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.ResponseService;
import com.spire.ms.System.Collections.Specialized.CollectionsUtil;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.*;

@Controller("/api/admin")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RolesAllowed("IS_ADMIN")
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Контроллер для модератора",
        description = "Содержит функциоал проверки постов, а так же имеется возможность банить пользователей"
)
public class ModeratorController extends BaseController{

    public static final Logger logger = LoggerFactory.getLogger(ModeratorController.class);

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение списка предложений аренды, которые необходимо проверить администратору")
    @Get(uri = "/post/get", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView({JsonViewCollector.Post.BasicView.class} )
    public HttpResponse<Page<Post>> getPosts(
            @QueryValue @Nullable String oid,
            @QueryValue @Nullable String locate,
            @QueryValue @Nullable String status,
            @QueryValue @Nullable Sort.Order.Direction dir,
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        try{
            if(oid != null)
                return HttpResponse.ok(
                        Page.of(
                                List.of(
                                    postRepository.findById(oid).orElseThrow(()-> new RuntimeException("Не найден пост с таким oid"))
                                ),
                                getPageable(page_num,page_size),
                                1
                        )
                );
            logger.info(getUserId() + " took a few posts check.");
            return HttpResponse.ok(postRepository.getFiltered(
                    locate == null? "" : locate,
                    status == null? "moderation" : status,
                    null,
                    "",
                    getPageable(page_num, page_size,new Sort.Order("postCreationDate", dir, false))
            ));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(
            summary = "Получение списка пользователей",
            description = "Данный метод используется администратором, для просмотра информации о пользователях"
    )
    @Get(uri = "/users/get", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.User.BasicView.class)
    public HttpResponse<Page<User>> getUsers(
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        try{
            logger.error(getUserId() + " took a few posts check.");
            return HttpResponse.ok(userRepository.findAll(getPageable(page_num,page_size)));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }
    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение списка предложений аренды, которые необходимо проверить администратору")
    @Patch(uri = "/post/change{?status}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView({JsonViewCollector.Post.BasicView.class} )
    public HttpResponse<AppResponseWithObject> change(
            @Body Optional<List<String>> oids,
            @QueryValue Optional<String> status
    ){
        try{

            List<String> success = new ArrayList<>();
            Map<String, String> failed = new HashMap<>();
            oids.orElseThrow(
                    ()-> new RuntimeException("В теле запроса отсутсвует список идентификаторов")
            ).forEach(it -> {
                logger.error(getUserId() + " tries to change the status of a post");

                Post target = postRepository.findById(it).orElse(null);

                if(target == null){
                    failed.put(it, "Не удаётся найти пост с данным идентификатором");
                    return;//Только остановит итерацию, а не весь перебор
                }

                target.setPostStatus(
                        status.orElseThrow(
                                () -> new RuntimeException("Статус не может быть null")
                        )
                );

                try{
                    postRepository.update(target);
                }catch (Exception ex){
                    ex.printStackTrace();
                    failed.put(it, ex.getMessage());
                }
                success.add(it);
            });

            return HttpResponse.ok(
                    responseService.successWithObject(
                            "Попытка изменения статуса завершена.",
                            CollectionUtils.mapOf(
                                    "success", success,
                                    "failed", failed
                            )
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение списка предложений аренды, которые необходимо проверить администратору")
    @Patch(uri = "/user/ban", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    public HttpResponse<AppResponseWithObject> banUser(
            @Body Optional<List<String>> oids
    ){
        try{
            List<String> success = new ArrayList<>();
            Map<String, String> failed = new HashMap<>();
            oids.orElseThrow(
                    ()-> new RuntimeException("В теле запроса отсутсвует список идентификаторов")
            ).forEach(it->{
                logger.error(getUserId() + " tries to change the status of a post");

                User target = userRepository.findById(it).orElse(null);

                if(target == null){
                    failed.put(it, "Не удаётся найти пользователя с данным идентификатором");
                    return;//Только остановит итерацию, а не весь перебор
                }

                target.setIsBanned(true);

                try{
                    userRepository.update(target);
                }catch (Exception ex){
                    ex.printStackTrace();
                    failed.put(it, ex.getMessage());
                }
                success.add(it);
            });
            return HttpResponse.ok(
                    responseService.successWithObject(
                            "Попытка изменения статуса завершена.",
                            CollectionUtils.mapOf(
                                    "success", success,
                                    "failed", failed
                            )
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }
}
