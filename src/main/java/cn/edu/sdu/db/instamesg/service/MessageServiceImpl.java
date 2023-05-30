package cn.edu.sdu.db.instamesg.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public Boolean sendToFriendMessage(int senderId, int receiverId, String text) {
        return true;
    }

    @Override
    public Boolean sendToFriendFile(int senderId, int receiverId, MultipartFile file) {
        return true;
    }

}