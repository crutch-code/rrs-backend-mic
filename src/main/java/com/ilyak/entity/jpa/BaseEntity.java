package com.ilyak.entity.jpa;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
@JsonView(JsonViewCollector.BaseEntity.class)
public abstract class BaseEntity {
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


    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof BaseEntity)) throw new IllegalArgumentException();
        return this.oid.equals(((BaseEntity) obj).oid);
    };

    @Override
    public int hashCode() {
        return Objects.hash(oid);
    }
}
