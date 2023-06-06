package cn.edu.sdu.db.instamesg.api;

import cn.edu.sdu.db.instamesg.pojo.Message;

public class groupMessageInfo extends ApiResponse {
    private UserInfo sender;
    private Message message;

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
