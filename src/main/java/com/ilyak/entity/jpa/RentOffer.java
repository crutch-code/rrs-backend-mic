package com.ilyak.entity.jpa;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.jackson.annotation.JacksonFeatures;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rent_offer", schema = "public")
@Introspected
@JsonView(JsonViewCollector.RentOffer.BasicView.class)
@DynamicInsert
@JacksonFeatures(additionalModules = JavaTimeModule.class)
public class RentOffer extends BaseEntity{


    @JsonInclude
    @JsonProperty("rent_offer_type")
    @Transient
    String type;

    @JsonInclude
    @JsonProperty("rent_offer_resolve")
    @Column(name = "rent_offer_resolve")
    @JsonAlias("rent_offer_resolve")
    Boolean resolve;

    @JsonInclude
    @JsonProperty("rent_offer_start")
    @Column(name = "rent_offer_start")
    @JsonView({
            JsonViewCollector.RentOffer.OnlyDates.class,
            JsonViewCollector.RentOffer.WithDates.class,
    })
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonAlias("rent_offer_start")
    LocalDateTime start;

    @JsonInclude
    @JsonProperty("rent_offer_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView({
            JsonViewCollector.RentOffer.OnlyDates.class,
            JsonViewCollector.RentOffer.WithDates.class
    })
    @Column(name = "rent_offer_end")
    @JsonAlias("rent_offer_end")
    LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "rent_offer_renter")
    @JsonProperty(value = "rent_offer_renter")
    @Schema(name = "rent_offer_renter")
    User renter;

    @ManyToOne
    @JoinColumn(name = "rent_offer_post")
    @JsonProperty(value = "rent_offer_post")
    @Schema(name = "rent_offer_post")
    Post post;

    public RentOffer(String oid, Boolean resolve, LocalDateTime start, LocalDateTime end, User renter, Post post) {
        super(oid);
        this.resolve = resolve;
        this.start = start;
        this.end = end;
        this.renter = renter;
        this.post = post;
    }

    public RentOffer() {
    }

    public Boolean getResolve() {
        return resolve;
    }

    public void setResolve(Boolean resolve) {
        this.resolve = resolve;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public User getRenter() {
        return renter;
    }

    public void setRenter(User renter) {
        this.renter = renter;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
