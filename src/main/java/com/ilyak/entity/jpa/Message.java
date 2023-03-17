package com.ilyak.entity.jpa;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.jackson.annotation.JacksonFeatures;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "message", schema = "public")
@Introspected
@JsonView
public class Message extends BaseEntity{

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "message_sender")
    @JsonProperty(value = "message_sender")
    @Schema(name = "message_sender")
    private User messageSender;

    @ManyToOne
    @JoinColumn(name = "message_chat_oid")
    @JsonProperty(value = "message_chat")
    @Schema(name = "message_chat")
    private Chat messageChat;

    @Column(name = "message_send_time")
    @JsonProperty(value = "message_send_time")
    @Schema(name = "message_send_time")
    private LocalDateTime messageSendTime;

    public Message() {
    }

    public Message(String oid, String message, User messageSender, Chat messageChat, LocalDateTime messageSendTime) {
        super(oid);
        this.message = message;
        this.messageSender = messageSender;
        this.messageChat = messageChat;
        this.messageSendTime = messageSendTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(User messageSender) {
        this.messageSender = messageSender;
    }

    public Chat getMessageChat() {
        return messageChat;
    }

    public void setMessageChat(Chat messageChat) {
        this.messageChat = messageChat;
    }

    public LocalDateTime getMessageSendTime() {
        return messageSendTime;
    }

    public void setMessageSendTime(LocalDateTime messageSendTime) {
        this.messageSendTime = messageSendTime;
    }
}