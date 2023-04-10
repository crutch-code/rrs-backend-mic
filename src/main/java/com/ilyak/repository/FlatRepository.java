package com.ilyak.repository;

import com.ilyak.entity.jpa.Flat;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.http.annotation.QueryValue;

import java.nio.file.OpenOption;
import java.util.Optional;

@Repository
public interface FlatRepository extends CrudRepository<Flat, String> {

    @Query(
            value = "from Flat as t where t.flatOwner.oid=:owner order by t.flatAddress",
            countQuery = "select count(t) from Flat as t where t.flatOwner.oid=:owner"
    )
    Page<Flat> findByFlatOwnerOid(String owner, Pageable pageable);
}
