package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.*;
import cn.edu.sdu.db.instamesg.dao.*;
import cn.edu.sdu.db.instamesg.filter.CORSFilter;
import cn.edu.sdu.db.instamesg.pojo.*;
import cn.edu.sdu.db.instamesg.service.FriendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {
    @Autowired
    private CORSFilter corsFilter;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @Autowired
    private WaitUserRepository waitUserRepository;

    @PostMapping("/addFriend")
    public synchronized ApiResponse addFriend(HttpSession session, @RequestParam(required = false) String username,
                                               @RequestParam(required = false) String email, @RequestParam(required = false) String reason) {
        if (session.getAttribute("user") == null) {
            return new SimpleResponse(false, "You are not logged in");
        }
        if(username == null && email == null) {
            return new SimpleResponse(false, "Username and email can't be null at the same time");
        }
        if(username != null && email != null) {
            return new SimpleResponse(false, "Username and email can't be not null at the same time");
        }
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        User friend;
        if(username != null)
            friend = userRepository.findByUsername(username);
        else
            friend = userRepository.findByEmail(email);
        if(friend == null)
            return new SimpleResponse(false, "Friend not found");
        Friend relationship = friendRepository.findById(new FriendId(userId, friend.getId())).orElse(null);
        if(relationship != null)
            return new SimpleResponse(false, "You are already friends");
        if(friend.getId().equals(userId))
            return new SimpleResponse(false, "You can't add yourself as friend");
        if(friendService.addFriend(user, friend, reason))
            return new SimpleResponse(true, "Add friend successfully, waiting for friend's confirmation");
        else
            return new SimpleResponse(false, "Add friend failed");
    }

    @GetMapping("/waitingFriendList")
    public synchronized ApiResponse waitingFriendList(HttpSession session) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        int userId = (int) session.getAttribute("user");
        List<WaitUser> waitList = waitUserRepository.findAllByFromId(userId);
        if(waitList == null || waitList.isEmpty())
            return new SimpleResponse(false, "No friend waiting");
        List<addFriendInfo> addFriendInfoList = new ArrayList<>();
        for(WaitUser waitUser : waitList) {
            User user = userRepository.findById(waitUser.getId().getUserToId()).orElse(null);
            if(user == null || user.getType().equals("deleted"))
                continue;
            addFriendInfoList.add(new addFriendInfo(
                    new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getType(), user.getRegisterTime()
            ),
                    waitUser.getReason(),
                    waitUser.getTime()));
        }
        return new DataResponse(true, "", addFriendInfoList);
    }

    @GetMapping("/confirmFriendList")
    public synchronized ApiResponse confirmFriendList(HttpSession session) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        int userId = (int) session.getAttribute("user");
        List<WaitUser> confirmList = waitUserRepository.findAllByToId(userId);
        if(confirmList == null || confirmList.isEmpty())
            return new SimpleResponse(false, "No friend confirmation");
        List<addFriendInfo> addFriendInfoList = new ArrayList<>();
        for(WaitUser waitUser : confirmList) {
            User user = userRepository.findById(waitUser.getId().getUserFromId()).orElse(null);
            if(user == null || user.getType().equals("deleted"))
                continue;
            addFriendInfoList.add(new addFriendInfo(
                    new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getType(), user.getRegisterTime()
            ),
                    waitUser.getReason(),
                    waitUser.getTime()));
        }
        return new DataResponse(true, "", addFriendInfoList);
    }

    @PostMapping("/acceptFriend")
    public synchronized ApiResponse acceptFriend(HttpSession session, @RequestParam String userid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        userid = userid.trim();
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        User friend = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(friend == null)
            return new SimpleResponse(false, "Friend not found");
        WaitUser waitUser = waitUserRepository.findById(new WaitUserId(friend.getId(), user.getId())).orElse(null);
        if(waitUser == null)
            return new SimpleResponse(false, "No friend confirmation");
        if(friendService.acceptFriend(user, friend))
            return new SimpleResponse(true, "Accept friend successfully");
        return new SimpleResponse(false, "Accept friend failed");
    }

    @PostMapping("/denyFriend")
    public synchronized ApiResponse denyFriend(HttpSession session, @RequestParam String userid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        userid = userid.trim();
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        User friend = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(friend == null)
            return new SimpleResponse(false, "Friend not found");
        WaitUser waitUser = waitUserRepository.findById(new WaitUserId(friend.getId(), user.getId())).orElse(null);
        if(waitUser == null)
            return new SimpleResponse(false, "No friend confirmation");
        if(friendService.denyFriend(user, friend))
            return new SimpleResponse(true, "Deny friend successfully");
        return new SimpleResponse(false, "Deny friend failed");
    }

    @PostMapping("/deleteFriend")
    public synchronized ApiResponse deleteFriend(HttpSession session, @RequestParam String toid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        toid = toid.trim();
        if(!toid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int fromId = (int) session.getAttribute("user");
        User fromUser = userRepository.findById(fromId).orElse(null), toUser = userRepository.findById(Integer.valueOf(toid)).orElse(null);
        if(toUser == null)
            return new SimpleResponse(false, "Friend not found");
        if(friendService.deleteFriend(fromUser, toUser))
            return new SimpleResponse(true, "Delete friend successfully");
        return new SimpleResponse(false, "Delete friend failed");
    }
}
