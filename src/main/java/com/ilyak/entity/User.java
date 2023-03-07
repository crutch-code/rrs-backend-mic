package com.ilyak.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.jsonviews.Default;
import com.ilyak.entity.jsonviews.WithPassword;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.jackson.annotation.JacksonFeatures;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users", schema = "public")
@Introspected
@JsonView(Default.class)
@DynamicInsert
@JacksonFeatures(additionalModules = JavaTimeModule.class)
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate userBirthday;

    @JsonInclude
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("user_reg_date")
    @Column(name = "user_reg_date")
    @JsonAlias("userRegDate")
    private LocalDate userRegDate;

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

    @ManyToMany
    @JoinTable(name = "users_avatar_file",
            joinColumns = @JoinColumn(name = "user_oid", referencedColumnName = "oid"),
            inverseJoinColumns = @JoinColumn(name = "file_oid", referencedColumnName = "oid"))
    @JsonInclude
    private Set<Files> avatars = new java.util.LinkedHashSet<>();

    @JsonInclude
    @JsonProperty("user_phone_number")
    @Column(name = "user_phone_number")
    @JsonAlias("userPhoneNumber")
    private String userPhoneNumber;

    @JsonInclude
    @JsonProperty("user_is_confirm")
    @Column(name = "user_is_confirm", nullable = false)
    @JsonAlias("userIsConfirm")
    private Boolean userIsConfirm;

    public User(
            String oid, String userName,
            LocalDate userBirthday, LocalDate userRegDate, String userEmail, String userPassword, Set<Files> avatars,
            String userPhoneNumber, Boolean userIsConfirm
    ) {
        super(oid);
        this.userName = userName;
        this.userBirthday = userBirthday;
        this.userRegDate = userRegDate;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.avatars = avatars;
        this.userPhoneNumber = userPhoneNumber;
        this.userIsConfirm = userIsConfirm;
    }

    public User() {

    }

    public Set<Files> getAvatars() {
        return avatars;
    }

    public void setAvatars(Set<Files> avatars) {
        this.avatars = avatars;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(LocalDate userBirthday) {
        this.userBirthday = userBirthday;
    }

    public LocalDate getUserRegDate() {
        return userRegDate;
    }

    public void setUserRegDate(LocalDate userRegDay) {
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
