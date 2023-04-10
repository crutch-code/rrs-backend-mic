package com.ilyak.repository;


import com.ilyak.entity.jpa.RentOffer;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public interface RentOfferRepository  extends CrudRepository<RentOffer, String> {

    @Query(
            value = "from RentOffer as t " +
                    "where t.post.oid =:oid " +
                    "and t.resolve = true " +
                    "and (cast(:start as timestamp) is null or t.start >=cast(:start as timestamp)) " +
                    "and (cast(:end as timestamp) is null or t.end <=cast(:end as timestamp)) ",
            countQuery = "select count(t) from RentOffer as t " +
                    "where t.post.oid =:oid " +
                    "and t.resolve = true " +
                    "and (cast(:start as timestamp) is null or t.start >=cast(:start as timestamp)) " +
                    "and (cast(:end as timestamp) is null or t.end <=cast(:end as timestamp)) "
    )
    Page<RentOffer> reservedDates(String oid, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
