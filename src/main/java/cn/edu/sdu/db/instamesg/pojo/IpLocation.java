package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;

@Entity
public class IpLocation {
    @EmbeddedId
    private IpLocationId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "IP", nullable = false, length = 64)
    private String ip;

    public IpLocationId getId() {
        return id;
    }

    public void setId(IpLocationId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}