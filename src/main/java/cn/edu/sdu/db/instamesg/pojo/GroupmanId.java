package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupmanId implements Serializable {
    private static final long serialVersionUID = 6579965529255839203L;
    @Column(name = "groupId", nullable = false)
    private Integer groupId;

    @Column(name = "managerId", nullable = false)
    private Integer managerId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GroupmanId entity = (GroupmanId) o;
        return Objects.equals(this.groupId, entity.groupId) &&
                Objects.equals(this.managerId, entity.managerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, managerId);
    }

}