package cn.edu.sdu.db.instamesg.api;

import java.time.Instant;

public class UserInfo {
    private Integer userId;
    private String userName;
    private String email;
    private String type;
    private String avatarPath;
    private Instant registerTime;

    public UserInfo(Integer userId, String userName, String email, String type, String avatarPath, Instant registerTime) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.type = type;
        this.avatarPath = avatarPath;
        this.registerTime = registerTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Instant registerTime) {
        this.registerTime = registerTime;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
