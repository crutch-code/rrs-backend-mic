package com.ilyak.repository;

import com.ilyak.entity.jpa.Contract;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Singleton;

@Repository
public interface ContractRepository extends CrudRepository<Contract, String> {

    @Query(
            value = "from Contract as t where t.renter =:oid or t.owner =:oid order by t.contractDate asc ",
            countQuery = "select count(t) from Contract as t where t.renter =:oid or t.owner =:oid"
    )
    Page<Contract> findByUsers(String oid, Pageable pageable);
}
