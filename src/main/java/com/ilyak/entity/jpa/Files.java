package com.ilyak.entity.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "files", schema = "public")
@Introspected
public class Files extends BaseEntity{

    @JsonInclude()
    @Column(name = "file_path")
    @JsonProperty(value = "file_path")
    public String filePath;

    @JsonInclude
    @Column(name = "file_size")
    @JsonProperty(value = "file_size")
    public Long fileSize;

    @JsonInclude
    @Column(name = "updated_at")
    @JsonProperty(value = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
    public LocalDateTime updatedAt;


    public Files(String oid, String filePath, Long fileSize, LocalDateTime updatedAt) {
        super(oid);
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.updatedAt = updatedAt;
    }

    public Files() {

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
