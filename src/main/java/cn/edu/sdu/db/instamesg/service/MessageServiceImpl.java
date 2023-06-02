package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.api.UserInfo;
import cn.edu.sdu.db.instamesg.api.friendMessageInfo;
import cn.edu.sdu.db.instamesg.dao.FriendmessageRepository;
import cn.edu.sdu.db.instamesg.dao.MessageRepository;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.pojo.Friendmessage;
import cn.edu.sdu.db.instamesg.pojo.Message;
import cn.edu.sdu.db.instamesg.pojo.User;
import cn.edu.sdu.db.instamesg.tools.messageEncryptAndDecrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FriendmessageRepository friendmessageRepository;

    @Override
    public Boolean sendToFriendMessage(int senderId, int receiverId, String text) {
        try {
            Message message = new Message();
            message.setContent(messageEncryptAndDecrypt.encryptMessage(text));
            message.setSendTime(Instant.now());
            message.setType("text");
            int messageId = messageRepository.save(message).getId();
            Friendmessage friendmessage = new Friendmessage();
            friendmessage.setReceiver(userRepository.findById(receiverId).orElse(null));
            friendmessage.setSender(userRepository.findById(senderId).orElse(null));
            message.setId(messageId);
            friendmessage.setMessages(message);
            friendmessageRepository.save(friendmessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Boolean sendToFriendFile(int senderId, int receiverId, MultipartFile file) {
        return true;
    }

    @Override
    public List<friendMessageInfo> listFriendMessage(int senderId, int receiverId) {
        List<Friendmessage> messageList = friendmessageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        messageList.addAll(friendmessageRepository.findBySenderIdAndReceiverId(receiverId, senderId));
        if(messageList.isEmpty())
            return null;
        List<friendMessageInfo> friendMessageInfoList = new ArrayList<>();
        for(Friendmessage message: messageList) {
            Message message1 = messageRepository.findById(message.getId()).orElse(null);
            if(message1 == null)
                continue;
            message1.setContent(messageEncryptAndDecrypt.decryptMessage(message1.getContent()));
            message.setMessages(message1);
            friendMessageInfo info = new friendMessageInfo();
            info.setSender(new UserInfo(message.getSender()));
            info.setReceiver(new UserInfo(message.getReceiver()));
            info.setMessage(message.getMessages());
            friendMessageInfoList.add(info);
        }
        friendMessageInfoList.sort(new Comparator<friendMessageInfo>() {
            @Override
            public int compare(friendMessageInfo o1, friendMessageInfo o2) {
                return o1.getMessage().getSendTime().compareTo(o2.getMessage().getSendTime());
            }
        });
        return friendMessageInfoList;
    }
}