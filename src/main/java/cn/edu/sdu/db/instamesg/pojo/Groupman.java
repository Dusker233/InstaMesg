package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "groupmans")
public class Groupman {
    @EmbeddedId
    private GroupmanId id;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    public Groupman() {
    }

    public Groupman(GroupmanId id, String status) {
        this.id = id;
        this.status = status;
    }

    public GroupmanId getId() {
        return id;
    }

    public void setId(GroupmanId id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}