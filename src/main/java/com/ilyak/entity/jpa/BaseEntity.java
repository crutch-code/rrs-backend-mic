package com.ilyak.entity.jpa;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;

import javax.persistence.*;

@MappedSuperclass
@JsonView(JsonViewCollector.BaseEntity.class)
public class BaseEntity {
    @Id
    @JsonInclude
    @Column(name= "oid")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    public String oid;

    public BaseEntity(String oid) {
        this.oid = oid;
    }

    public BaseEntity() {
        this.oid = oid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
