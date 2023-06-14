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
            value = "from Post as post_ " +
                    "where post_.postFlat.flatAddress like concat('%', :location, '%') " +
                    "and post_.postStatus like concat('%', :status, '%') "+
                    "and (:uid is null or post_.postCreator.oid =:uid)" +
                    "and post_.postFlat.flatType like concat('%', :flat, '%') ",
            countQuery = "select count(post_) from Post as post_ " +
                        "where post_.postFlat.flatAddress like concat('%', :location, '%') " +
                        "and post_.postStatus=:status " +
                        "and (:uid is null or post_.postCreator.oid =:uid)" +
                        "and post_.postFlat.flatType like concat('%', :flat, '%') "
    )
    Page<Post> getFiltered(
            String location,
            @Nullable String status,
            @Nullable String uid,
            @Nullable String flat,
            Pageable pageable
    );

//    @Query(
//            value = "from Post as t " +
//                    "where t.postFlat.flatAddress like concat('%', :location, '%') " +
//                    "and t.postStatus like concat('%', :status, '%') "+
//                    "and (:uid is null or t.postCreator.oid =:uid)" +
//                    "and t.postFlat.flatType like concat('%', :flat, '%') ",
//            countQuery = "select count(t) from Post as t "
//    )
//    Page<Post> getFiltered(
//            String location,
//            @Nullable String status,
//            @Nullable String uid,
//            @Nullable String flat,
//            Sort sort,
//            Pageable pageable
//    );
//


    @Query(value = "select exists(select from post as t where t.post_creator_oid=:uid " +
            "and t.oid=:chatOid)", nativeQuery = true)
    Boolean valid(String uid, String chatOid);

}
