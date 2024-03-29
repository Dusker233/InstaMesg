package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Embeddable
public class GrouprecordId implements Serializable {
    private static final long serialVersionUID = -1966269232249588728L;
    @NotNull
    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @NotNull
    @Column(name = "manager_id", nullable = false)
    private Integer managerId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull
    @Column(name = "add_time", nullable = false)
    private Instant addTime;

    public GrouprecordId() {
    }

    public GrouprecordId(Group group, User executor, User user) {
        this.groupId = group.getId();
        this.managerId = executor.getId();
        this.userId = user.getId();
        this.addTime = Instant.now();
    }

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Instant getAddTime() {
        return addTime;
    }

    public void setAddTime(Instant addTime) {
        this.addTime = addTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GrouprecordId entity = (GrouprecordId) o;
        return Objects.equals(this.addTime, entity.addTime) &&
                Objects.equals(this.groupId, entity.groupId) &&
                Objects.equals(this.managerId, entity.managerId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addTime, groupId, managerId, userId);
    }

}