package com.ilyak.repository;

import com.ilyak.entity.jpa.Post;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;

@Repository
public interface PostRepository extends CrudRepository<Post, String> {

    @Query(
            value = "from Post as t " +
                    "where t.postFlat.flatAddress like concat('%', :location, '%') " +
                    "and t.postStatus=:status "+
                    "and (:uid is null or t.postCreator.oid =:uid)",
            countQuery = "select count(t) from Post as t " +
                        "where t.postFlat.flatAddress like concat('%', :location, '%') " +
                        "and t.postStatus=:status " +
                        "and (:uid is null or t.postCreator.oid =:uid)"
    )
    Page<Post> getFiltered(
            String location,
            String status,
            @Nullable String uid,
            Sort sort,
            Pageable pageable
    );

    @Query(value = "select exists(select from post as t where t.post_creator_oid=:uid " +
            "and t.oid=:chatOid)", nativeQuery = true)
    Boolean valid(String uid, String chatOid);

}
