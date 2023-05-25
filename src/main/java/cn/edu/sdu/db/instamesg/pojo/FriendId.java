package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendId implements Serializable {
    private static final long serialVersionUID = -82164189498201521L;
    @NotNull
    @Column(name = "usera_id", nullable = false)
    private Integer useraId;

    @NotNull
    @Column(name = "userb_id", nullable = false)
    private Integer userbId;

    public FriendId() {
    }

    public FriendId(Integer fromId, Integer toId) {
        this.useraId = fromId;
        this.userbId = toId;
    }

    public Integer getUseraId() {
        return useraId;
    }

    public void setUseraId(Integer useraId) {
        this.useraId = useraId;
    }

    public Integer getUserbId() {
        return userbId;
    }

    public void setUserbId(Integer userbId) {
        this.userbId = userbId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FriendId entity = (FriendId) o;
        return Objects.equals(this.userbId, entity.userbId) &&
                Objects.equals(this.useraId, entity.useraId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userbId, useraId);
    }

}