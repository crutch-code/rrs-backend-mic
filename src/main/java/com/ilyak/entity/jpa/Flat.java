package com.ilyak.entity.jpa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.jackson.annotation.JacksonFeatures;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "flat", schema = "public")
@Introspected
@JsonView(JsonViewCollector.Default.class)
@DynamicInsert
@JacksonFeatures(additionalModules = JavaTimeModule.class)
public class Flat extends BaseEntity{


    @ManyToOne
    @Schema(name = "flat_owner")
    @JsonProperty("flat_owner")
    @JoinColumn(name = "flat_owner")
    @JsonView(JsonViewCollector.Flat.WithFlatOwner.class)
    private User flatOwner;


    private Double square;

    @Schema(name = "rooms_count")
    @JsonProperty("rooms_count")
    private Integer roomsCount;

    @Schema(name = "flat_x_map_cord")
    @JsonProperty("flat_x_map_cord")
    private Double flatXMapCord;

    @Schema(name = "flat_y_map_cord")
    @JsonProperty("flat_y_map_cord")
    private Double flatYMapCord;

    public Flat(String oid, User flatOwner, Double square, Integer roomsCount, Double flatXMapCord, Double flatYMapCord) {
        super(oid);
        this.flatOwner = flatOwner;
        this.square = square;
        this.roomsCount = roomsCount;
        this.flatXMapCord = flatXMapCord;
        this.flatYMapCord = flatYMapCord;
    }

    public Flat() {

    }

    public User getFlatOwner() {
        return flatOwner;
    }

    public void setFlatOwner(User flatOwner) {
        this.flatOwner = flatOwner;
    }

    public Double getSquare() {
        return square;
    }

    public void setSquare(Double square) {
        this.square = square;
    }

    public Integer getRoomsCount() {
        return roomsCount;
    }

    public void setRoomsCount(Integer roomsCount) {
        this.roomsCount = roomsCount;
    }

    public Double getFlatXMapCord() {
        return flatXMapCord;
    }

    public void setFlatXMapCord(Double flatXMapCord) {
        this.flatXMapCord = flatXMapCord;
    }

    public Double getFlatYMapCord() {
        return flatYMapCord;
    }

    public void setFlatYMapCord(Double flatYMapCord) {
        this.flatYMapCord = flatYMapCord;
    }
}
