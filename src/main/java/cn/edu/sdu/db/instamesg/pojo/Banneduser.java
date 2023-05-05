package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bannedusers")
public class Banneduser {
    @EmbeddedId
    private BanneduserId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "ban_reason")
    private String banReason;

    @MapsId("executor")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "executor", nullable = false)
    private User executor;

    public BanneduserId getId() {
        return id;
    }

    public void setId(BanneduserId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }
}