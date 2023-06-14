package com.ilyak.entity.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Introspected
@Table(name = "rating", schema = "public")
@JsonView({JsonViewCollector.Rating.BasicView.class})
public class Rating extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_oid", nullable = false)
    @JsonProperty("user_oid")
    @Schema(name = "user_oid")
    private User userOid;


    @Column(name = "rating_score", nullable = false)
    @JsonProperty("rating_score")
    @Schema(name = "rating_score")
    private BigDecimal ratingScore;

    public Rating(String oid, User userOid, BigDecimal ratingScore) {
        super(oid);
        this.userOid = userOid;
        this.ratingScore = ratingScore;
    }

    public Rating() {
    }
}