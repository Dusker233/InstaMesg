package cn.edu.sdu.db.instamesg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.edu.sdu.db.instamesg.api.ApiResponse;
import cn.edu.sdu.db.instamesg.api.SimpleResponse;
import cn.edu.sdu.db.instamesg.dao.FriendRepository;
import cn.edu.sdu.db.instamesg.dao.GroupuserRepository;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.service.MessageService;
import jakarta.servlet.http.HttpSession;

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

    @PostMapping("/sendFriend")
    public synchronized ApiResponse sendMessageToFriend(HttpSession session, @RequestParam String userid, @RequestParam String content, @RequestParam(required = false) MultipartFile file) {
        if(session.getAttribute("user") == null)
        	return new SimpleResponse(false, "please log in first");
        int senderId = (int) session.getAttribute("user");
        int receiverId = Integer.parseInt(userid);
        if(friendRepository.findByUserAIdandUserBId(senderId, receiverId) != null) {
            if(friendRepository.findByUserAIdandUserBId(receiverId, senderId) == null)
            	return new SimpleResponse(false, "You've been deleted or blacklisted by the other");
            if(file != null) {
                if(messageService.sendToFriendMessage(senderId, receiverId, content) && messageService.sendToFriendFile(senderId, receiverId, file))
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
