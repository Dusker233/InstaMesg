package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {
    @EmbeddedId
    private FriendId id;

    @MapsId("useraId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usera_id", nullable = false)
    private User usera;

    @MapsId("userbId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userb_id", nullable = false)
    private User userb;

    public FriendId getId() {
        return id;
    }

    public void setId(FriendId id) {
        this.id = id;
    }

    public User getUsera() {
        return usera;
    }

    public void setUsera(User usera) {
        this.usera = usera;
    }

    public User getUserb() {
        return userb;
    }

    public void setUserb(User userb) {
        this.userb = userb;
    }

}