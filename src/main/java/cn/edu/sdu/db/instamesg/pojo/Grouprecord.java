package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "grouprecords")
public class Grouprecord {
    @EmbeddedId
    private GrouprecordId id;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    public Grouprecord(Group group, User executor, User user, String status) {
        this.id = new GrouprecordId(group, executor, user);
        this.status = status;
    }

    public GrouprecordId getId() {
        return id;
    }

    public void setId(GrouprecordId id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}