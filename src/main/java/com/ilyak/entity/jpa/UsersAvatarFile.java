package com.ilyak.entity.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "users_avatar_file")
public class UsersAvatarFile extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_oid", nullable = false)
    private User userOid;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "file_oid", nullable = false)
    private Files fileOid;

    public UsersAvatarFile() {
    }

    public UsersAvatarFile(String oid, User userOid, Files fileOid) {
        super(oid);
        this.userOid = userOid;
        this.fileOid = fileOid;
    }

    public User getUserOid() {
        return userOid;
    }

    public void setUserOid(User userOid) {
        this.userOid = userOid;
    }

    public Files getFileOid() {
        return fileOid;
    }

    public void setFileOid(Files fileOid) {
        this.fileOid = fileOid;
    }
}