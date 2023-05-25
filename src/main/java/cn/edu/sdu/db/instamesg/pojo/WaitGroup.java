package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "wait_group")
public class WaitGroup {
    @EmbeddedId
    private WaitGroupId id;

    @Lob
    @Column(name = "reason")
    private String reason;

    @NotNull
    @Column(name = "time", nullable = false)
    private Instant time;

    public WaitGroup() {
    }

    public WaitGroup(User user, Group group, String reason) {
        this.id = new WaitGroupId(user.getId(), group.getId());
        this.reason = reason;
        this.time = Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8)));
    }

    public WaitGroupId getId() {
        return id;
    }

    public void setId(WaitGroupId id) {
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