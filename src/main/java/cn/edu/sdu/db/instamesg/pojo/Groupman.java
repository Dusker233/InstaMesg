package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "groupmans")
public class Groupman {
    @EmbeddedId
    private GroupmanId id;

    public GroupmanId getId() {
        return id;
    }

    public void setId(GroupmanId id) {
        this.id = id;
    }

}