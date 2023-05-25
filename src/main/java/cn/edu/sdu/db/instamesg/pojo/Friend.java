package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {
    @EmbeddedId
    private FriendId id;

    public Friend() {
    }

    public Friend(User user, User friend) {
        this.id = new FriendId(user.getId(), friend.getId());
    }

    public FriendId getId() {
        return id;
    }

    public void setId(FriendId id) {
        this.id = id;
    }

}