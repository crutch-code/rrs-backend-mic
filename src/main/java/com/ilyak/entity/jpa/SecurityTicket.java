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
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "security_ticket", schema = "public")
@Introspected
@JsonView(JsonViewCollector.Default.class)
@DynamicInsert
@JacksonFeatures(additionalModules = JavaTimeModule.class)
public class SecurityTicket  extends BaseEntity{


    @Column(name = "security_ticket_last_updated_at")
    @JsonProperty("updated_at")
    @Schema(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToMany
    @JoinTable(name = "security_ticket_photos",
            joinColumns = @JoinColumn(name = "security_ticket_oid", referencedColumnName = "oid"),
            inverseJoinColumns = @JoinColumn(name = "files_oid", referencedColumnName = "oid"))
    @JsonProperty("security_ticket_photos")
    @Schema(name = "security_ticket_photos")
    private List<Files> securityTicketPhotos;

    @Column(name = "security_ticket_status")
    private String status;

    @Column(name = "security_ticket_description")
    private String description;


    public SecurityTicket(String oid, LocalDateTime updatedAt, List<Files> securityTicketPhotos, String status, String description) {
        super(oid);
        this.updatedAt = updatedAt;
        this.securityTicketPhotos = securityTicketPhotos;
        this.status = status;
        this.description = description;
    }

    public SecurityTicket() {
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Files> getSecurityTicketPhotos() {
        return securityTicketPhotos;
    }

    public void setSecurityTicketPhotos(List<Files> securityTicketPhotos) {
        this.securityTicketPhotos = securityTicketPhotos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
