package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.*;
import cn.edu.sdu.db.instamesg.dao.*;
import cn.edu.sdu.db.instamesg.filter.CORSFilter;
import cn.edu.sdu.db.instamesg.pojo.Group;
import cn.edu.sdu.db.instamesg.pojo.Groupman;
import cn.edu.sdu.db.instamesg.pojo.User;
import cn.edu.sdu.db.instamesg.pojo.WaitUser;
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
    private GroupRepository groupRepository;

    @Autowired
    private WaitUserRepository waitUserRepository;

    @Autowired
    private GroupmanRepository groupmanRepository;

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
        User Friend;
        if(username != null)
            Friend = userRepository.findByUsername(username);
        else
            Friend = userRepository.findByEmail(email);
        if(Friend == null)
            return new SimpleResponse(false, "Friend not found");
        if(friendService.addFriend(user, Friend, reason))
            return new SimpleResponse(true, "Add friend successfully, waiting for friend's confirmation");
        else
            return new SimpleResponse(false, "Add friend failed");
    }

    @PostMapping("/addGroup")
    public synchronized ApiResponse addGroup(HttpSession session, @RequestParam(required = false) String groupId,
                                             @RequestParam(required = false) String groupName, @RequestParam(required = false) String reason) {
        if (session.getAttribute("user") == null) {
            return new SimpleResponse(false, "You are not logged in");
        }
        if(groupId == null && groupName == null) {
            return new SimpleResponse(false, "GroupId and groupName can't be null at the same time");
        }
        if(groupId != null && groupName != null) {
            return new SimpleResponse(false, "GroupId and groupName can't be not null at the same time");
        }
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        Group group;
        if(groupId != null)
            group = groupRepository.findGroupById(Integer.valueOf(groupId.trim()));
        else
            group = groupRepository.findGroupByGroupName(groupName);
        if(group == null)
            return new SimpleResponse(false, "Group not found");
        if(friendService.addGroup(user, group, reason))
            return new SimpleResponse(true, "Add group successfully, waiting for group managers' confirmation");
        else
            return new SimpleResponse(false, "Add group failed");
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
        if(friendService.denyFriend(user, friend))
            return new SimpleResponse(true, "Deny friend successfully");
        return new SimpleResponse(false, "Deny friend failed");
    }

    @PostMapping("/acceptGroup")
    public synchronized ApiResponse acceptGroup(HttpSession session, @RequestParam String executorId, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        executorId = executorId.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!executorId.matches("\\d+"))
            return new SimpleResponse(false, "Invalid executorId");
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        User executor = userRepository.findById(Integer.valueOf(executorId)).orElse(null);
        if(executor == null)
            return new SimpleResponse(false, "Executor not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        Groupman candidate = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        if(candidate == null)
            return new SimpleResponse(false, "Executor is not a group manager of this group");
        if(friendService.acceptGroup(user, executor, group))
            return new SimpleResponse(true, "Accept group successfully");
        return new SimpleResponse(false, "Accept group failed");
    }

    @PostMapping("/denyGroup")
    public synchronized ApiResponse denyGroup(HttpSession session, @RequestParam String executorId, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        executorId = executorId.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!executorId.matches("\\d+"))
            return new SimpleResponse(false, "Invalid executorId");
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        User executor = userRepository.findById(Integer.valueOf(executorId)).orElse(null);
        if(executor == null)
            return new SimpleResponse(false, "Executor not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        Groupman candidate = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        if(candidate == null)
            return new SimpleResponse(false, "Executor is not a group manager of this group");
        if(friendService.denyGroup(user, executor, group))
            return new SimpleResponse(true, "Accept group successfully");
        return new SimpleResponse(false, "Accept group failed");
    }

    @PostMapping("/deleteFriend")
    public synchronized ApiResponse deleteFriend(HttpSession session, @RequestParam String userid) {
        // TODO do sth
        return null;
    }

    @PostMapping("/deleteGroup")
    public synchronized ApiResponse deleteGroup(HttpSession session, @RequestParam String groupid) {
        //TODO do sth
        return null;
    }
}
