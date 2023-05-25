package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WaitUserId implements Serializable {
    private static final long serialVersionUID = -1187604948021185501L;
    @NotNull
    @Column(name = "user_from_id", nullable = false)
    private Integer userFromId;

    @NotNull
    @Column(name = "user_to_id", nullable = false)
    private Integer userToId;

    public WaitUserId() {
    }

    public WaitUserId(Integer id, Integer id1) {
        this.userFromId = id;
        this.userToId = id1;
    }

    public Integer getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(Integer userFromId) {
        this.userFromId = userFromId;
    }

    public Integer getUserToId() {
        return userToId;
    }

    public void setUserToId(Integer userToId) {
        this.userToId = userToId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WaitUserId entity = (WaitUserId) o;
        return Objects.equals(this.userFromId, entity.userFromId) &&
                Objects.equals(this.userToId, entity.userToId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userFromId, userToId);
    }

}