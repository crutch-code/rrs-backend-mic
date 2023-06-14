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
@JsonView(JsonViewCollector.BaseEntity.Default.class)
@DynamicInsert
@JacksonFeatures(additionalModules = JavaTimeModule.class)
public class Flat extends BaseEntity{


    @ManyToOne
    @Schema(name = "flat_owner")
    @JsonProperty("flat_owner")
    @JoinColumn(name = "flat_owner")
    @JsonView(JsonViewCollector.Flat.BasicView.class)
    private User flatOwner;

    @JsonProperty(value = "flat_address")
    @Schema(name = "flat_address")
    @Column(name = "flat_address")
    private String flatAddress;

    @Column(name = "flat_square")
    private Double square;

    @Schema(name = "rooms_count")
    @JsonProperty("rooms_count")
    @Column(name = "flat_rooms_count")
    private Integer roomsCount;

    @Schema(name = "flat_x_map_cord")
    @JsonProperty("flat_x_map_cord")
    @Column(name = "flat_x_map_cord")
    private Double flatXMapCord;

    @Schema(name = "flat_y_map_cord")
    @JsonProperty("flat_y_map_cord")
    @Column(name = "flat_y_map_cord")
    private Double flatYMapCord;

    @Schema(name = "flat_type")
    @JsonProperty("flat_type")
    @Column(name = "flat_type")
    private String flatType;

    public Flat(String oid, User flatOwner, String flatAddress, Double square, Integer roomsCount, Double flatXMapCord, Double flatYMapCord, String flatType) {
        super(oid);
        this.flatOwner = flatOwner;
        this.flatAddress = flatAddress;
        this.square = square;
        this.roomsCount = roomsCount;
        this.flatXMapCord = flatXMapCord;
        this.flatYMapCord = flatYMapCord;
        this.flatType = flatType;
    }

    public Flat() {

    }

    public String getFlatAddress() {
        return flatAddress;
    }

    public void setFlatAddress(String flatAddress) {
        this.flatAddress = flatAddress;
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

    public String getFlatType() {
        return flatType;
    }

    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }
}
