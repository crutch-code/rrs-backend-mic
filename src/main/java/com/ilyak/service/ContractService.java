package com.ilyak.service;

import com.ilyak.repository.TransactionalRepository;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Map;


@Singleton
public class ContractService {

    @Inject
    TransactionalRepository transactionalRepository;

    Map<String, String > resolveAnchors(String renter, String owner){
        return CollectionUtils.mapOf();
    }
}
