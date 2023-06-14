package com.ilyak.entity.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.service.PushService;

import java.time.LocalDateTime;
import java.util.Arrays;

@JsonView(JsonViewCollector.PushMessage.BasicView.class)
public class PushMessage{

    PushService.MessageType type;

    String sender;

    String target;

    String oid;

    Object content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    public PushMessage(
            PushService.MessageType type,
            String sender,
            String target,
            LocalDateTime timestamp,
            Object content
    ) {
        this.type = type;
        this.sender = sender;
        this.target = target;
        this.oid = oid;
        this.content = content;
        this.timestamp = timestamp;
    }

    public PushMessage() {
    }

    public PushService.MessageType getType() {
        return type;
    }

    public void setType(PushService.MessageType type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
