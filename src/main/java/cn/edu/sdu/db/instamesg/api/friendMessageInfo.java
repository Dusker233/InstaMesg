package cn.edu.sdu.db.instamesg.api;

import cn.edu.sdu.db.instamesg.pojo.Message;

public class friendMessageInfo extends ApiResponse {
    private UserInfo sender;
    private UserInfo receiver;
    private Message message;

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public UserInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(UserInfo receiver) {
        this.receiver = receiver;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
