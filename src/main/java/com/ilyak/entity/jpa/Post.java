package com.ilyak.entity.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.jackson.annotation.JacksonFeatures;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "post", schema = "public")
@Introspected
@JsonView({JsonViewCollector.Post.BasicView.class, JsonViewCollector.Post.FullyUser.class})
@DynamicInsert
@JacksonFeatures(additionalModules = JavaTimeModule.class)
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
    @JoinColumn(name = "post_flat_oid", nullable = false, updatable = false)
    @JsonProperty(value = "post_flat")
    @Schema(name = "post_flat")
    private Flat postFlat;

    @Column(name = "post_creation_date")
    @JsonProperty(value = "post_creation_date")
    @Schema(name = "post_creation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postCreationDate;

    @ManyToMany
    @JoinTable(name = "post_photos",
            joinColumns = @JoinColumn(name = "post_oid", referencedColumnName = "oid"),
            inverseJoinColumns = @JoinColumn(name = "file_oid", referencedColumnName = "oid"))
    @JsonInclude
    @JsonProperty(value = "post_photos")
    @Schema(name = "post_photos")
    private Set<Files> postPhotos = new java.util.LinkedHashSet<>();

    public Post(String oid, String postStatus, Double price, String postTitle, String postInformation, User postCreator, Flat postFlat, LocalDateTime postCreationDate, Set<Files> postPhotos) {
        super(oid);
        this.postStatus = postStatus;
        this.price = price;
        this.postTitle = postTitle;
        this.postInformation = postInformation;
        this.postCreator = postCreator;
        this.postFlat = postFlat;
        this.postCreationDate = postCreationDate;
        this.postPhotos = postPhotos;
    }

    public Post() {
    }

    public LocalDateTime getPostCreationDate() {
        return postCreationDate;
    }

    public void setPostCreationDate(LocalDateTime postCreationDate) {
        this.postCreationDate = postCreationDate;
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

    public Flat getPostFlat() {
        return postFlat;
    }

    public void setPostFlat(Flat postFlat) {
        this.postFlat = postFlat;
    }

    public Set<Files> getPostPhotos() {
        return postPhotos;
    }

    public void setPostPhotos(Set<Files> postPhotos) {
        this.postPhotos = postPhotos;
    }
}
