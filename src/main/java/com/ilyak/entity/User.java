package com.ilyak.entity;

import com.fasterxml.jackson.annotation.*;
import com.ilyak.entity.jsonviews.Default;
import com.ilyak.entity.jsonviews.WithPassword;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.annotation.Part;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "users", schema = "public")
@Introspected
@JsonView(Default.class)
@DynamicInsert
public class User extends BaseEntity {
    @JsonInclude
    @JsonProperty("user_name")
    @Column(name = "user_name")
    @JsonAlias("userName")
    private String userName;
    @JsonInclude
    @JsonProperty("user_birthday")
    @Column(name = "user_birthday")
    @JsonAlias("userBirthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date userBirthday;

    @JsonInclude
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("user_reg_date")
    @Column(name = "user_reg_date")
    @JsonAlias("userRegDate")
    private Date userRegDate;

    @JsonInclude
    @JsonProperty("user_email")
    @Column(name = "user_email")
    @JsonAlias("userEmail")
    private String userEmail;


    @JsonProperty("user_password")
    @Column(name = "user_password")
    @JsonAlias("userPassword")
    @JsonView(WithPassword.class)
    private String userPassword;

    @ManyToOne
    @JoinColumn(name = "avatar_path", nullable = false, columnDefinition = "default '2303010801231333'")
//    @JoinColumn(name = "avatar_path", nullable = false)
    @JsonProperty("avatar_path")
    @JsonInclude
    @JsonAlias("avatarPath")
    private Files avatarPath;

    @JsonInclude
    @JsonProperty("user_phone_number")
    @Column(name = "user_phone_number")
    @JsonAlias("userPhoneNumber")
    private String userPhoneNumber;

    @JsonInclude
    @JsonProperty("user_is_confirm")
//    @ColumnDefault("default 'false'")
//    @Generated(GenerationTime.INSERT)
    @Column(name = "user_is_confirm", nullable = false)
    @JsonAlias("userIsConfirm")
    private Boolean userIsConfirm;

    public User(
            String oid, String userName,
            Date userBirthday, Date userRegDate, String userEmail, String userPassword, Files avatarPath,
            String userPhoneNumber, Boolean userIsConfirm
    ) {
        super(oid);
        this.userName = userName;
        this.userBirthday = userBirthday;
        this.userRegDate = userRegDate;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.avatarPath = avatarPath;
        this.userPhoneNumber = userPhoneNumber;
        this.userIsConfirm = userIsConfirm;
    }

    public User() {

    }

    public Files getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(Files avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    public Date getUserRegDate() {
        return userRegDate;
    }

    public void setUserRegDate(Date userRegDay) {
        this.userRegDate = userRegDay;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getUserIsConfirm() {
        return userIsConfirm;
    }

    public void setUserIsConfirm(Boolean userIsConfirm) {
        this.userIsConfirm = userIsConfirm;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
