package cn.edu.sdu.db.instamesg.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @Column(name = "message_id", nullable = false)
    private Integer id;

    @NotNull
    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "picture")
    private String picture;

    @NotNull
    @Column(name = "send_time", nullable = false)
    private Instant sendTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Instant getSendTime() {
        return sendTime;
    }

    public void setSendTime(Instant sendTime) {
        this.sendTime = sendTime;
    }

}