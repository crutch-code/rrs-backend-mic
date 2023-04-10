package com.ilyak.entity.jpa;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat", schema = "public")
@Introspected
@JsonView(JsonViewCollector.Chat.BasicView.class)
public class Chat extends BaseEntity{

    @Column(name = "chat_creation_date")
    @JsonProperty(value = "chat_creation_date")
    @Schema(name = "chat_creation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(name = "chat_last_activity")
    @JsonProperty(value = "chat_last_activity")
    @Schema(name = "chat_last_activity")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActivity;

    @ManyToOne
    @JoinColumn(name = "chat_left_user_recipient")
    @JsonProperty(value = "chat_left_user_recipient")
    @Schema(name = "chat_left_user_recipient")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    private User leftRecipient;

    @ManyToOne
    @JoinColumn(name = "chat_right_user_recipient")
    @JsonProperty(value = "chat_right_user_recipient")
    @Schema(name = "chat_right_user_recipient")
    @JsonView(JsonViewCollector.BaseEntity.Default.class)
    private User rightRecipient;



    public Chat() {
    }

    public Chat(String oid, LocalDateTime creationDate, LocalDateTime lastActivity, User leftRecipient, User rightRecipient, Message lastMessage) {
        super(oid);
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.leftRecipient = leftRecipient;
        this.rightRecipient = rightRecipient;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public User getLeftRecipient() {
        return leftRecipient;
    }

    public void setLeftRecipient(User leftRecipient) {
        this.leftRecipient = leftRecipient;
    }

    public User getRightRecipient() {
        return rightRecipient;
    }

    public void setRightRecipient(User rightRecipient) {
        this.rightRecipient = rightRecipient;
    }
}
