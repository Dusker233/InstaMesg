package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendId implements Serializable {
    private static final long serialVersionUID = -1987478338097701605L;
    @Column(name = "userAId", nullable = false)
    private Integer userAId;

    @Column(name = "userBId", nullable = false)
    private Integer userBId;

    public Integer getUserAId() {
        return userAId;
    }

    public void setUserAId(Integer userAId) {
        this.userAId = userAId;
    }

    public Integer getUserBId() {
        return userBId;
    }

    public void setUserBId(Integer userBId) {
        this.userBId = userBId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FriendId entity = (FriendId) o;
        return Objects.equals(this.userBId, entity.userBId) &&
                Objects.equals(this.userAId, entity.userAId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userBId, userAId);
    }

}