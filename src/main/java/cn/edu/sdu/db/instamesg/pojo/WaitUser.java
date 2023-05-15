package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "wait_user")
public class WaitUser {
    @EmbeddedId
    private WaitUserId id;

    @Lob
    @Column(name = "reason")
    private String reason;

    @NotNull
    @Column(name = "time", nullable = false)
    private Instant time;

    public WaitUser(User user, User friend, String reason) {
        this.id = new WaitUserId(user.getId(), friend.getId());
        this.reason = reason;
        this.time = Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8)));
    }

    public WaitUserId getId() {
        return id;
    }

    public void setId(WaitUserId id) {
        this.id = id;
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