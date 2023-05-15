package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "groupusers")
public class Groupuser {
    @EmbeddedId
    private GroupuserId id;

    public Groupuser(User user, Group group) {
        this.id = new GroupuserId(group.getId(), user.getId());
    }

    public GroupuserId getId() {
        return id;
    }

    public void setId(GroupuserId id) {
        this.id = id;
    }

}