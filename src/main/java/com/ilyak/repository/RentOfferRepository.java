package com.ilyak.repository;


import com.ilyak.entity.jpa.RentOffer;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
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
                    "and t.start >= :startRent " +
                    "and t.end <= :endRent ",
            countQuery = "select count(t) from RentOffer as t " +
                    "where t.post.oid =:oid " +
                    "and t.resolve = true " +
                    "and t.start >= :startRent " +
                    "and t.end <= :endRent "
    )
    Page<RentOffer> reservedDates(String oid, @Nullable LocalDateTime startRent, @Nullable LocalDateTime endRent, Pageable pageable);

    @Query(
            value = "from RentOffer t where t.renter.oid = :oid or t.post.postCreator.oid = :oid",
            countQuery = "select count(t) from RentOffer t where t.renter.oid = :oid or t.post.postCreator.oid = :oid"
    )
    Page<RentOffer> findByUser(String oid, Pageable pageable);
}
