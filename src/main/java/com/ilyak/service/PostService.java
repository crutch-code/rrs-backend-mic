package com.ilyak.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ilyak.entity.websocket.PushMessage;
import com.ilyak.entity.jpa.Contract;
import com.ilyak.entity.jpa.RentOffer;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.*;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

@Singleton
public class PostService {

    @Inject
    DocumentService documentService;

    public static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Inject
    ResponseService responseService;
    @Inject
    ContractRepository contractRepository;

    @Inject
    PostRepository postRepository;

    @Inject
    FilesRepository filesRepository;

    @Inject
    TransactionalRepository transactionalRepository;

    public Boolean valid(String uid, String oid){
        return postRepository.valid(uid, oid);
    }




//    public PushMessage resolve(RentOffer offer){
//
//
//
//        if (offer.getResolve() == null){
//            offer.setOid(transactionalRepository.genOid().orElseThrow());
//            offer.setPost(postRepository.findById(offer.getPost().getOid()).orElseThrow());
//            offerRepository.save(offer);
//            return new PushMessage(
//                    PushMessage.PushType.POST,
//                    offer.getRenter().getOid(),
//                    offer.getPost().getPostCreator().getOid(),
//                    offer.getPost().getOid(),
//                    offer,
//                    LocalDateTime.now(ZoneId.systemDefault())
//            );
//        }
//        offerRepository.update(offer);
//
//        if(offer.getResolve()){
//            Contract target = generateContract(offer);
//            target.setDocument(
//                    documentService.genDocument(
//                            CollectionUtils.mapOf(
//                                    "conclusion_day", target.getContractDate().format(DateTimeFormatter.ofPattern("dd")),
//                                    "conclusion_month", target.getContractDate().format(DateTimeFormatter.ofPattern("MM")),
//                                    "conclusion_year", target.getContractDate().format(DateTimeFormatter.ofPattern("yy")),
//                                    "owner", target.getOwner().getUserName(),
//                                    "renter", target.getRenter().getUserName(),
//                                    "flat_square", target.getTargetFlat().getSquare(),
//                                    "flat_address", target.getTargetFlat().getFlatAddress(),
//                                    "post_tag", target.getTargetPost().getOid(),
//                                    "total_cost", target.getTotalCost(),
//                                    "total_cost_plain_text", target.getTotalCostFlat()
//                            )
//                    )
//            );
//            contractRepository.save(target);
//        }
//
//        return new PushMessage(
//                PushMessage.PushType.POST,
//                offer.getPost().getPostCreator().getOid(),
//                offer.getRenter().getOid(),
//                offer.getPost().getOid(),
//                offer,
//                LocalDateTime.now(ZoneId.systemDefault())
//        );
//    }

    public Runnable generateDocument(Contract contract){
        return ()-> {
            Map<String, Object> anchors = new java.util.HashMap<>(Map.of(
                    "conclusion_day", contract.getContractDate().format(DateTimeFormatter.ofPattern("dd")),
                    "conclusion_month", contract.getContractDate().format(DateTimeFormatter.ofPattern("MM")),
                    "conclusion_year", contract.getContractDate().format(DateTimeFormatter.ofPattern("yy")),
                    "owner", contract.getOwner().getUserName(),
                    "renter", contract.getRenter().getUserName(),
                    "flat_square", contract.getTargetFlat().getSquare(),
                    "flat_address", contract.getTargetFlat().getFlatAddress(),
                    "post_tag", contract.getTargetPost().getOid(),
                    "total_cost", contract.getTotalCost(),
                    "total_cost_plain_text", contract.getTotalCostFlat()
            ));
            anchors.put("day_start", contract.getStart().getDayOfMonth());
            anchors.put("month_start", contract.getStart().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            anchors.put("year_start", contract.getStart().getYear());
            anchors.put("day_end", contract.getEnd().getDayOfMonth());
            anchors.put("month_end", contract.getEnd().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            anchors.put("year_end", contract.getEnd().getYear());
            contract.setDocument(documentService.genDocument(anchors));
            contract.getDocument().setOid(transactionalRepository.genOid().orElseThrow());
            filesRepository.save(contract.getDocument());
            contractRepository.update(contract);
        };
    }

    public Contract generateContract(RentOffer offer){

        double totalCost = Math.abs(ChronoUnit.DAYS.between(offer.getEnd(), offer.getStart()) * offer.getPost().getPrice());
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


}
