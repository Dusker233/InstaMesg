package cn.edu.sdu.db.instamesg.service;

import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    public Boolean sendToFriendMessage(int senderId, int receiverId, String text);

    public Boolean sendToFriendFile(int senderId, int receiverId, MultipartFile file);
}