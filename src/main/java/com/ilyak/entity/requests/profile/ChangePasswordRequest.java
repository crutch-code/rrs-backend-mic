package com.ilyak.entity.requests.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class ChangePasswordRequest {

    @JsonInclude
    @Schema(name = "old_password")
    @JsonProperty(value = "old_password")
    private String oldPassword;
    @JsonInclude
    @Schema(name = "new_password")
    @JsonProperty(value = "new_password")
    private String newPassword;
    @JsonInclude
    private boolean kill;

    public ChangePasswordRequest(String oldPassword, String newPassword, boolean kill) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.kill = kill;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
    }

    public boolean isKill() {
        return kill;
    }
}
