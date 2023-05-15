package cn.edu.sdu.db.instamesg.api;

import java.time.Instant;

public class addFriendInfo {
    private UserInfo userInfo;
    private String reason;
    private Instant time;

    public addFriendInfo(UserInfo userInfo, String reason, Instant time) {
        this.userInfo = userInfo;
        this.reason = reason;
        this.time = time;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
