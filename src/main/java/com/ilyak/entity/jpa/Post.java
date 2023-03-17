package com.ilyak.entity.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;

@Entity
@Table(name = "post", schema = "public")
@Introspected
@JsonView(JsonViewCollector.Default.class)
public class Post extends BaseEntity {

    @Schema(name = "post_status")
    @Column(name = "post_status")
    @JsonProperty(value = "post_status")
    private String postStatus;

    @Schema(name = "price")
    @Column(name = "price")
    @JsonProperty(value = "price")
    private Double price;

    @Schema(name = "post_title")
    @Column(name = "post_title")
    @JsonProperty(value = "post_title")
    private String postTitle;

    @Schema(name = "post_information")
    @Column(name = "post_information")
    @JsonProperty(value = "post_information")
    private String postInformation;

    @ManyToOne
    @JoinColumn(name = "post_creator_oid")
    @JsonProperty(value = "post_creator")
    @Schema(name = "post_creator")
    private User postCreator;

    @ManyToOne
    @JoinColumn(name = "post_moderator_oid", updatable = false)
    @JsonView(JsonViewCollector.WithModerator.class)
    @JsonProperty(value = "post_moderator")
    @Schema(name = "post_moderator")
    private User postModerator;

    @ManyToOne
    @JoinColumn(name = "post_flat_oid", nullable = false, updatable = false)
    @JsonProperty(value = "post_flat")
    @Schema(name = "post_flat")
    private Flat postFlat;

    public Post(String oid, String postStatus, Double price, String postTitle, String postInformation, User postCreator, User postModerator, Flat postFlat) {
        super(oid);
        this.postStatus = postStatus;
        this.price = price;
        this.postTitle = postTitle;
        this.postInformation = postInformation;
        this.postCreator = postCreator;
        this.postModerator = postModerator;
        this.postFlat = postFlat;
    }

    public Post() {
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostInformation() {
        return postInformation;
    }

    public void setPostInformation(String postInformation) {
        this.postInformation = postInformation;
    }

    public User getPostCreator() {
        return postCreator;
    }

    public void setPostCreator(User postCreator) {
        this.postCreator = postCreator;
    }

    public User getPostModerator() {
        return postModerator;
    }

    public void setPostModerator(User postModerator) {
        this.postModerator = postModerator;
    }

    public Flat getPostFlat() {
        return postFlat;
    }

    public void setPostFlat(Flat postFlat) {
        this.postFlat = postFlat;
    }
}
