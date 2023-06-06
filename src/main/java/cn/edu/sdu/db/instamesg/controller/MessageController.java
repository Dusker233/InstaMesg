package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.DataResponse;
import cn.edu.sdu.db.instamesg.api.friendMessageInfo;
import cn.edu.sdu.db.instamesg.pojo.Friendmessage;
import cn.edu.sdu.db.instamesg.tools.messageEncryptAndDecrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cn.edu.sdu.db.instamesg.api.ApiResponse;
import cn.edu.sdu.db.instamesg.api.SimpleResponse;
import cn.edu.sdu.db.instamesg.dao.FriendRepository;
import cn.edu.sdu.db.instamesg.dao.GroupuserRepository;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.service.MessageService;
import jakarta.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private GroupuserRepository groupuserRepository;

    @Autowired
    private MessageService messageService;

    @GetMapping("/listFriendMessage")
    public synchronized ApiResponse listFriendMessage(HttpSession session, @RequestParam String userid) throws UnsupportedEncodingException {
        if(session.getAttribute("user") == null)
        	return new SimpleResponse(false, "please log in first");
        int senderId = (int) session.getAttribute("user");
        int receiverId = Integer.parseInt(userid);
        if(friendRepository.findByUserAIdandUserBId(senderId, receiverId) != null) {
            if(friendRepository.findByUserAIdandUserBId(receiverId, senderId) == null)
            	return new SimpleResponse(false, "You've been deleted or blacklisted by the other");
            List<friendMessageInfo> list = messageService.listFriendMessage(senderId, receiverId);
            if(list != null)
                return new DataResponse(true, "", list);
            return new SimpleResponse(false, "No message");
        }
        return new SimpleResponse(false, "You don't have the friend relationship with this user");
    }

	@GetMapping("/listGroupMessage")
    public synchronized ApiResponse(HttpSession session, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
        	return new SimpleResponse(false, "please log in first");
        int groupId = Integer.parseInt(groupid);
        int userId = (int) session.getAttribute("user");
        if(groupuserRepository.findByUseridAndGroupId(userId, groupId) != null) {
            List<groupMessageInfo> list = messageService.listGroupMessage(groupId);
            if(list != null)
            	return new SimpleResponse(true, "", list);
            return new SimpleResponse(false, "No message");
        }
        return new SimpleResponse(false, "You are not in this group");
    }

    @GetMapping("/encryptTest")
    public synchronized ApiResponse encryptTest(@RequestParam String text) throws UnsupportedEncodingException {
        return new DataResponse(true, "", messageEncryptAndDecrypt.encryptMessage(text));
    }

    @GetMapping("/decryptTest")
    public synchronized ApiResponse decryptTest(@RequestParam String text) throws UnsupportedEncodingException {
        return new DataResponse(true, "", messageEncryptAndDecrypt.decryptMessage(text));
    }

    @PostMapping("/sendFriend")
    public synchronized ApiResponse sendMessageToFriend(HttpSession session, @RequestParam String userid, @RequestParam(required = false) String content, @RequestParam(required = false) MultipartFile file) {
        if(session.getAttribute("user") == null)
        	return new SimpleResponse(false, "please log in first");
        if(content == null && file == null)
        	return new SimpleResponse(false, "please input content or upload file");
        if(content != null && file != null)
        	return new SimpleResponse(false, "please input content or upload file, not both");
        int senderId = (int) session.getAttribute("user");
        int receiverId = Integer.parseInt(userid);
        if(friendRepository.findByUserAIdandUserBId(senderId, receiverId) != null) {
            if(friendRepository.findByUserAIdandUserBId(receiverId, senderId) == null)
            	return new SimpleResponse(false, "You've been deleted or blacklisted by the other");
            if(file != null) {
                if(messageService.sendToFriendFile(senderId, receiverId, file))
                	return new SimpleResponse(true, "send success");
                return new SimpleResponse(false, "send failed");
            } else {
                if(messageService.sendToFriendMessage(senderId, receiverId, content))
                	return new SimpleResponse(true, "send success");
                return new SimpleResponse(false, "send failed");
            }
        }
        return new SimpleResponse(false, "You don't have the friend relationship with this user");
    }
}
