package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "iplocation")
public class IpLocation {
    @EmbeddedId
    private IpLocationId id;

    @Size(max = 64)
    @NotNull
    @Column(name = "ip", nullable = false, length = 64)
    private String ip;

    public IpLocationId getId() {
        return id;
    }

    public void setId(IpLocationId id) {
        this.id = id;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}