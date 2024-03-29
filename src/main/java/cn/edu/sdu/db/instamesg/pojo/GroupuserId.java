package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupuserId implements Serializable {
    private static final long serialVersionUID = -4817266787274014177L;
    @NotNull
    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    public GroupuserId() {
    }

    public GroupuserId(Integer groupId, Integer userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GroupuserId entity = (GroupuserId) o;
        return Objects.equals(this.groupId, entity.groupId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }

}