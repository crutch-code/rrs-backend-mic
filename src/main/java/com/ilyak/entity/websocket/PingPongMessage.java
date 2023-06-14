package com.ilyak.entity.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class PingPongMessage {

    Type type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    public PingPongMessage() {
    }

    public PingPongMessage(Type type, LocalDateTime timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    @JsonProperty("type")
    public String getType() {
        return type.name();
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = Type.valueOf(type);
    }

    public enum Type{
        PING, PONG


    }
}
