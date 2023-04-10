package com.ilyak.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ilyak.entity.PushMessage;
import com.ilyak.entity.jpa.Contract;
import com.ilyak.entity.jpa.RentOffer;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.ContractRepository;
import com.ilyak.repository.PostRepository;
import com.ilyak.repository.RentOfferRepository;
import com.ilyak.repository.TransactionalRepository;
import com.spire.ms.System.Collections.Specialized.CollectionsUtil;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Locale;

@Singleton
public class PostService {

    @Inject
    public DocumentService documentService;

    public static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Inject
    ResponseService responseService;
    @Inject
    ContractRepository contractRepository;

    @Inject
    RentOfferRepository offerRepository;
    @Inject
    PostRepository postRepository;
    @Inject
    TransactionalRepository transactionalRepository;

    public Boolean valid(String uid, String oid){
        return postRepository.valid(uid, oid);
    }

    public PushMessage resolve(Object content){
        Object converted = converter(content, RentOffer.class);
        if(converted instanceof RentOffer){
            return  rentOfferResolve((RentOffer) converted);
        }
        throw new InternalExceptionResponse(responseService.error("Invalid post content type: " + content.getClass()));
    }


    private PushMessage rentOfferResolve(RentOffer offer){



        if (offer.getResolve() == null){
            offer.setOid(transactionalRepository.genOid().orElseThrow());
            offer.setPost(postRepository.findById(offer.getPost().getOid()).orElseThrow());
            offerRepository.save(offer);
            return new PushMessage(
                    PushService.PushType.POST,
                    offer.getRenter().getOid(),
                    offer.getPost().getPostCreator().getOid(),
                    offer.getPost().getOid(),
                    offer,
                    LocalDateTime.now(ZoneId.systemDefault())
            );
        }
        offerRepository.update(offer);

        if(offer.getResolve()){
            Contract target = generateContract(offer);
            target.setDocument(
                    documentService.genDocument(
                            CollectionUtils.mapOf(
                                    "conclusion_day", target.getContractDate().format(DateTimeFormatter.ofPattern("dd")),
                                    "conclusion_month", target.getContractDate().format(DateTimeFormatter.ofPattern("MM")),
                                    "conclusion_year", target.getContractDate().format(DateTimeFormatter.ofPattern("yy")),
                                    "owner", target.getOwner().getUserName(),
                                    "renter", target.getRenter().getUserName(),
                                    "flat_square", target.getTargetFlat().getSquare(),
                                    "flat_address", target.getTargetFlat().getFlatAddress(),
                                    "post_tag", target.getTargetPost().getOid(),
                                    "total_cost", target.getTotalCost(),
                                    "total_cost_plain_text", target.getTotalCostFlat()
                            )
                    )
            );
            contractRepository.save(target);
        }

        return new PushMessage(
                PushService.PushType.POST,
                offer.getPost().getPostCreator().getOid(),
                offer.getRenter().getOid(),
                offer.getPost().getOid(),
                offer,
                LocalDateTime.now(ZoneId.systemDefault())
        );
    }

    private Contract generateContract(RentOffer offer){

        double totalCost = ChronoUnit.DAYS.between(offer.getEnd(), offer.getStart()) * offer.getPost().getPrice();
        return new Contract(
                transactionalRepository.genOid().orElseThrow(),
                LocalDateTime.now(ZoneId.systemDefault()),
                offer.getStart(),
                offer.getEnd(),
                offer.getRenter(),
                offer.getPost().getPostCreator(),
                offer.getPost().getPostFlat(),
                offer.getPost(),
                totalCost,
                new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT).format(totalCost),
                null
        );
    }

    private Object converter(Object target, Class<?> to){
        Object res = null;
        try {
            ObjectMapper m = new ObjectMapper().registerModule(new JavaTimeModule());
            res = m.readerWithView(JsonViewCollector.RentOffer.WithDates.class)
                    .readValue(m.writerWithView(JsonViewCollector.RentOffer.WithDates.class).writeValueAsString(target), to);
        }catch (Exception ex){
            logger.error(ex.getMessage());
        }
        return res;
    }
}
