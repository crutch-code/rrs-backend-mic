package com.ilyak.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jpa.Contract;
import com.ilyak.entity.jpa.Post;
import com.ilyak.entity.jpa.RentOffer;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.entity.websocket.PushMessage;
import com.ilyak.repository.RentOfferRepository;
import com.ilyak.service.PushService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.validation.Validated;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Controller("/api/offer")
@Tag(name = "Контроллер заявок на аренду",
        description = "Данный котроллер используется для откликов на объявление"
)
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class RentOfferController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(RentOfferController.class);

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить все заявки на аренду пользователя")
    @Get(uri = "/get", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.RentOffer.WithDates.class)
    public Page<RentOffer> getContracts(
            @QueryValue @Nullable Optional<String> oid,
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        if(oid.isPresent()){
            return Page.of(
                    List.of(
                            rentOfferRepository.findById(oid.get()).orElseThrow(()-> new RuntimeException("Оффер с таким oid не найден"))
                    ),
                    getPageable(page_num, page_size),
                    1
            );
        }
        logger.info("Call /api/offer/get: " + getUserId());
        return rentOfferRepository.findByUser(getUserId(), getPageable(page_num, page_size));
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить все заявки на аренду пользователя")
    @io.micronaut.http.annotation.Post(uri = "/create", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Contract.BasicView.class)
    public HttpResponse<DefaultAppResponse> create(
            @Body RentOffer offer
    ){
        logger.info("Call /api/offer/create: " + getUserId());
        try{
            if(offer.getPost() == null){
                throw new RuntimeException("Не передан пост");
            }
            Post post = postRepository.findById(offer.getPost().getOid()).orElseThrow(()-> new RuntimeException("Пост с таким oid не найден"));
            User sender = getCurrentUser();

            offer.setOid(transactionalRepository.genOid().orElseThrow());
            offer.setRenter(sender);
            rentOfferRepository.save(offer);
            pushService.send(
                    new PushMessage(
                            PushService.MessageType.OFFER,
                            sender.getOid(),
                            post.getPostCreator().getOid(),
                            LocalDateTime.now(ZoneId.systemDefault()),
                            "Вам поступило предложение аренды"
                    )
            );
            return HttpResponse.ok(responseService.success("Отклик добавлен."));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(),ex, responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить все заявки на аренду пользователя")
    @Get(uri = "/resolve{?oid,accept}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Contract.BasicView.class)
    public HttpResponse<DefaultAppResponse> patch(
            @QueryValue Optional<String> oid,
            @QueryValue Optional<Boolean> accept
    ){
        logger.info("Call /api/contracts/resolve: " + getUserId());
        try{

            RentOffer target = rentOfferRepository.findById(
                    oid.orElseThrow(()-> new RuntimeException("Не передан oid оффера"))
            ).orElseThrow(()-> new RuntimeException("Оффер с таким oid не найден"));

            User owner = getCurrentUser();
            if (!target.getPost().getPostCreator().equals(owner))
                throw new RuntimeException("Этот пользователь не может управлять этим откликом");

            target.setResolve(accept.orElseThrow(()-> new RuntimeException("Не передан accept для подтверждения или откланения предложения аренды")));

            if(accept.get()){
                new Thread(
                        postService.generateDocument(
                                contractRepository.save(
                                        postService.generateContract(target)
                                )
                        )
                ).start();

                Schedulers.newThread().scheduleDirect(
                        ()-> {},
                        Duration.between(target.getStart(), target.getEnd()).toMillis(),
                        TimeUnit.MILLISECONDS
                );
            }
            rentOfferRepository.update(target);
            pushService.send(
                    new PushMessage(
                            PushService.MessageType.OFFER,
                            owner.getOid(),
                            target.getRenter().getOid(),
                            LocalDateTime.now(ZoneId.systemDefault()),
                            Map.of(
                                "message", "Предложение аренды рассмотрено",
                                "rent_offer", target.getOid(),
                                "accept", accept.get()
                            )
                    )
            );
            return HttpResponse.ok(responseService.success(
                    accept.get()? "Предложение принято. Позже можно будет скачать контракт":
                            "Преждложение отклонено"
            ));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(),ex, responseService.error(ex.getMessage()));
        }
    }
}
