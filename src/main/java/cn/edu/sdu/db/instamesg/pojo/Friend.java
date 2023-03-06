package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {
    @EmbeddedId
    private FriendId id;

    @MapsId("userAId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userAId", nullable = false)
    private User userAId;

    @MapsId("userBId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userBId", nullable = false)
    private User userBId;

    public FriendId getId() {
        return id;
    }

    public void setId(FriendId id) {
        this.id = id;
    }

    public User getUserAId() {
        return userAId;
    }

    public void setUserAId(User userAId) {
        this.userAId = userAId;
    }

    public User getUserBId() {
        return userBId;
    }

    public void setUserBId(User userBId) {
        this.userBId = userBId;
    }

}