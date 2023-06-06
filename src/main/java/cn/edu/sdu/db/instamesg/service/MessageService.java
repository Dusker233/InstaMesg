package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.api.friendMessageInfo;
import cn.edu.sdu.db.instamesg.api.groupMessageInfo;
import cn.edu.sdu.db.instamesg.pojo.Friendmessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MessageService {
    public List<friendMessageInfo> listFriendMessage(int senderId, int receiverId) throws UnsupportedEncodingException;

	public List<groupMessageInfo> listGroupMessage(int groupId);

    public Boolean sendToFriendMessage(int senderId, int receiverId, String text);

    public Boolean sendToFriendFile(int senderId, int receiverId, MultipartFile file);
}