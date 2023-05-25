package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.ApiResponse;
import cn.edu.sdu.db.instamesg.api.DataResponse;
import cn.edu.sdu.db.instamesg.api.SimpleResponse;
import cn.edu.sdu.db.instamesg.dao.*;
import cn.edu.sdu.db.instamesg.pojo.*;
import cn.edu.sdu.db.instamesg.service.GroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupuserRepository groupuserRepository;

    @Autowired
    private GroupmanRepository groupmanRepository;

    @Autowired
    private GroupService groupService;
    @Autowired
    private WaitGroupRepository waitGroupRepository;

    @PostMapping("/addGroup")
    public synchronized ApiResponse addGroup(HttpSession session, @RequestParam(required = false) String groupid,
                                             @RequestParam(required = false) String groupname, @RequestParam(required = false) String reason) {
        if (session.getAttribute("user") == null) {
            return new SimpleResponse(false, "You are not logged in");
        }
        if(groupid == null && groupname == null) {
            return new SimpleResponse(false, "GroupId and groupName can't be null at the same time");
        }
        if(groupid != null && groupname != null) {
            return new SimpleResponse(false, "GroupId and groupName can't be not null at the same time");
        }
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        Group group;
        if(groupid != null)
            group = groupRepository.findGroupById(Integer.valueOf(groupid.trim()));
        else
            group = groupRepository.findGroupByGroupName(groupname);
        if(group == null)
            return new SimpleResponse(false, "Group not found");
        if(groupuserRepository.findById(new GroupuserId(group.getId(), userId)).orElse(null) != null)
            return new SimpleResponse(false, "You are already in the group");
        if(groupService.addGroup(user, group, reason))
            return new SimpleResponse(true, "Add group successfully, waiting for group managers' confirmation");
        else
            return new SimpleResponse(false, "Add group failed");
    }

    @GetMapping("/waitingGroupList")
    public synchronized ApiResponse waitingGroupList(HttpSession session) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        int userId = (int) session.getAttribute("user");
        List<WaitGroup> waitGroupList = waitGroupRepository.findAllByUserId(userId);
        if(waitGroupList == null || waitGroupList.isEmpty())
            return new SimpleResponse(false, "No waiting group");
        return new DataResponse(true, "", waitGroupList);
    }

    @GetMapping("/confirmGroupList")
    public synchronized ApiResponse confirmGroupList(HttpSession session) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        int userId = (int) session.getAttribute("user");
        List<Groupman> groupmanList = groupmanRepository.findAllByUserid(userId);
        List<Integer> groupList = new ArrayList<>();
        for(Groupman groupman : groupmanList) {
            groupList.add(groupman.getId().getGroupId());
        }
        List<WaitGroup> waitGroupList = new ArrayList<>();
        for(Integer groupId : groupList) {
            waitGroupList.addAll(waitGroupRepository.findAllByGroupId(groupId));
        }
        if(waitGroupList.isEmpty())
            return new SimpleResponse(false, "No waiting group");
        return new DataResponse(true, "", waitGroupList);
    }

    @PostMapping("/acceptGroup")
    public synchronized ApiResponse acceptGroup(HttpSession session, @RequestParam String userid, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        userid = userid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User executor = userRepository.findById(userId).orElse(null);
        User user = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(user == null)
            return new SimpleResponse(false, "User not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        Groupman candidate = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        if(candidate == null)
            return new SimpleResponse(false, "You're not a group manager of this group");
        if(groupService.acceptGroup(user, executor, group))
            return new SimpleResponse(true, "Accept group successfully");
        return new SimpleResponse(false, "Accept group failed");
    }

    @PostMapping("/denyGroup")
    public synchronized ApiResponse denyGroup(HttpSession session, @RequestParam String userid, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        userid = userid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User executor = userRepository.findById(userId).orElse(null);
        User user = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(user == null)
            return new SimpleResponse(false, "User not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        Groupman candidate = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        if(candidate == null)
            return new SimpleResponse(false, "Executor is not a group manager of this group");
        if(groupService.denyGroup(user, executor, group))
            return new SimpleResponse(true, "Accept group successfully");
        return new SimpleResponse(false, "Accept group failed");
    }

    @PostMapping("/deleteGroup")
    public synchronized ApiResponse deleteGroup(HttpSession session, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        if(!groupRepository.findByUserIdAndGroupId(userId, group.getId()))
            return new SimpleResponse(false, "This user isn't exist in the group");
        Groupman groupman = groupmanRepository.findByGroupidAndUserid(Integer.valueOf(groupid), userId);
        if(groupman.getStatus().equals("master"))
            return new SimpleResponse(false, "redirect to destroyGroup");
        if(groupService.deleteGroup(user, group))
            return new SimpleResponse(true, "Delete group successfully");
        return new SimpleResponse(false, "Delete group failed");
    }

    @PostMapping("/destroyGroup")
    public synchronized ApiResponse destroyGroup(HttpSession session, @RequestParam String groupid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        int userId = (int) session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        Groupman groupman = groupmanRepository.findByGroupidAndUserid(group.getId(), userId);
        if(groupman == null || groupman.getStatus().equals("manager"))
            return new SimpleResponse(false, "You don't have permission to destroy this group");
        if(groupService.destroyGroup(user, group))
            return new SimpleResponse(true, "Destroy group successfully");
        return new SimpleResponse(false, "Destroy group failed");
    }

    @PostMapping("/setGroupManager")
    public synchronized ApiResponse setGroupManager(HttpSession session, @RequestParam String groupid, @RequestParam String userid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        userid = userid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User executor = userRepository.findById(userId).orElse(null);
        User user = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(user == null)
            return new SimpleResponse(false, "User not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        if(group == null)
            return new SimpleResponse(false, "Group not found");
        Groupman relation = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        if(relation == null)
            return new SimpleResponse(false, "Executor is not a group manager of this group");
        if(groupService.setGroupManager(user, executor, group))
            return new SimpleResponse(true, "Set group manager successfully");
        return new SimpleResponse(false, "Set group manager failed");
    }

    @PostMapping("/setGroupMaster")
    public synchronized ApiResponse setGroupMaster(HttpSession session, @RequestParam String groupid, @RequestParam String userid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        userid = userid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User executor = userRepository.findById(userId).orElse(null);
        User user = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(user == null)
            return new SimpleResponse(false, "User not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        if(group == null)
            return new SimpleResponse(false, "Group not found");
        Groupman relation = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        if(relation == null)
            return new SimpleResponse(false, "Executor is not a group manager of this group");
        if(!relation.getStatus().equals("master"))
            return new SimpleResponse(false, "Executor is not the group master of this group");
        if(groupService.setGroupMaster(user, executor, group))
            return new SimpleResponse(true, "Set group master successfully");
        return new SimpleResponse(false, "Set group master failed");
    }

    @PostMapping("/revokeGroupManager")
    public synchronized ApiResponse revokeGroupManager(HttpSession session, @RequestParam String groupid, @RequestParam String userid) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "You are not logged in");
        groupid = groupid.trim();
        userid = userid.trim();
        if(!groupid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid groupid");
        if(!userid.matches("\\d+"))
            return new SimpleResponse(false, "Invalid userid");
        int userId = (int) session.getAttribute("user");
        User executor = userRepository.findById(userId).orElse(null);
        User user = userRepository.findById(Integer.valueOf(userid)).orElse(null);
        if(user == null)
            return new SimpleResponse(false, "User not found");
        Group group = groupRepository.findGroupById(Integer.valueOf(groupid));
        if(group == null)
            return new SimpleResponse(false, "Group not found");
        Groupman relation1 = groupmanRepository.findByGroupidAndUserid(group.getId(), executor.getId());
        Groupman relation2 = groupmanRepository.findByGroupidAndUserid(group.getId(), user.getId());
        if(relation1 == null)
            return new SimpleResponse(false, "You don't have the permission to revoke a group manager");
        if(relation2 == null)
            return new SimpleResponse(false, "User is not a group manager of this group");
        if(groupService.revokeGroupManager(user, executor, group))
            return new SimpleResponse(true, "Revoke group manager successfully");
        return new SimpleResponse(false, "Revoke group manager failed");
    }
}
