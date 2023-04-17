package com.ilyak.entity.jpa;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract", schema = "public")
@Introspected
@JsonView(JsonViewCollector.Contract.BasicView.class)
public class Contract extends BaseEntity{

    @Column(name = "contract_date")
    @JsonProperty(value = "contract_date")
    @Schema(name = "contract_date")
    private LocalDateTime contractDate;

    @Column(name = "contract_start_rent")
    @JsonProperty(value = "contract_start_rent")
    @Schema(name = "contract_start_rent")
    private LocalDateTime start;

    @Column(name = "contract_end_rent")
    @JsonProperty(value = "contract_end_rent")
    @Schema(name = "contract_end_rent")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "contract_renter_oid")
    @JsonProperty(value = "contract_renter_oid")
    @Schema(name = "contract_renter_oid")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    private User renter;

    @ManyToOne
    @JoinColumn(name = "contract_owner_oid")
    @JsonProperty(value = "contract_owner_oid")
    @Schema(name = "contract_owner_oid")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "contrcat_target_flat")
    @JsonProperty(value = "contrcat_target_flat")
    @Schema(name = "contrcat_target_flat")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    private Flat targetFlat;

    @ManyToOne
    @JoinColumn(name = "contract_target_post")
    @JsonProperty(value = "contract_target_post")
    @Schema(name = "contract_target_post")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    private Post targetPost;

    @Column(name = "contract_total_cost")
    @JsonProperty(value = "contract_total_cost")
    @Schema(name = "contract_total_cost")
    private Double totalCost;

    @Column(name = "contract_total_cost_flat")
    @JsonProperty(value = "contract_total_cost_flat")
    @Schema(name = "contract_total_cost_flat")
    private String totalCostFlat;

    @ManyToOne
    @JoinColumn(name = "contract_document")
    @JsonProperty(value = "contract_document")
    @Schema(name = "contract_document")
    private Files document;

    public Contract(String oid, LocalDateTime contractDate, LocalDateTime start, LocalDateTime end,
                    User renter, User owner, Flat targetFlat, Post targetPost, Double totalCost, String totalCostFlat, Files document) {
        super(oid);
        this.contractDate = contractDate;
        this.start = start;
        this.end = end;
        this.renter = renter;
        this.owner = owner;
        this.targetFlat = targetFlat;
        this.targetPost = targetPost;
        this.totalCost = totalCost;
        this.totalCostFlat = totalCostFlat;
        this.document = document;
    }

    public Contract() {
    }

    public LocalDateTime getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDateTime contractDate) {
        this.contractDate = contractDate;
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

    public void setRenter(User creator) {
        this.renter = creator;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User signatory) {
        this.owner = signatory;
    }

    public Flat getTargetFlat() {
        return targetFlat;
    }

    public void setTargetFlat(Flat targetFlat) {
        this.targetFlat = targetFlat;
    }

    public Post getTargetPost() {
        return targetPost;
    }

    public void setTargetPost(Post targetPost) {
        this.targetPost = targetPost;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getTotalCostFlat() {
        return totalCostFlat;
    }

    public void setTotalCostFlat(String totalCostFlat) {
        this.totalCostFlat = totalCostFlat;
    }

    public Files getDocument() {
        return document;
    }

    public void setDocument(Files document) {
        this.document = document;
    }
}
