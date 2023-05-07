package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bannedusers")
public class Banneduser {
    @EmbeddedId
    private BanneduserId id;

    @Lob
    @Column(name = "ban_reason")
    private String banReason;

    @NotNull
    @Column(name = "executor", nullable = false)
    private Integer executor;

    public Integer getExecutor() {
        return executor;
    }

    public void setExecutor(Integer executor) {
        this.executor = executor;
    }

    public BanneduserId getId() {
        return id;
    }

    public void setId(BanneduserId id) {
        this.id = id;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

}