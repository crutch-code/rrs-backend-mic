package com.ilyak.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.http.multipart.CompletedPart;
import io.micronaut.jackson.annotation.JacksonFeatures;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@MappedSuperclass
public class BaseEntity {
    @Id
    @JsonInclude
    @Column(name= "oid")
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
