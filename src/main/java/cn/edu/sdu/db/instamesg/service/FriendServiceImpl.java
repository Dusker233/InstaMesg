package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.dao.*;
import cn.edu.sdu.db.instamesg.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService{
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private WaitUserRepository waitUserRepository;

    @Autowired
    private GroupuserRepository groupuserRepository;

    @Autowired
    private WaitGroupRepository waitGroupRepository;

    @Autowired
    private GroupRecordRepository groupRecordRepository;

    @Override
    public Boolean addFriend(User user, User friend, String reason) {
        try {
            if(friendRepository.findByUserAIdandUserBId(user.getId(), friend.getId()) != null)
                return false;
            waitUserRepository.save(new WaitUser(user, friend, reason));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean addGroup(User user, Group group, String reason) {
        try {
            if(groupuserRepository.findByUseridAndGroupid(user.getId(), group.getId()) != null)
                return false;
            waitGroupRepository.save(new WaitGroup(user, group, reason));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean acceptFriend(User user, User friend) {
        try {
            waitUserRepository.deleteById(new WaitUserId(user.getId(), friend.getId()));
            friendRepository.save(new Friend(user, friend));
            friendRepository.save(new Friend(friend, user));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean denyFriend(User user, User friend) {
        try {
            waitUserRepository.deleteById(new WaitUserId(user.getId(), friend.getId()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean acceptGroup(User user, User executor, Group group) {
        try {
            waitGroupRepository.deleteById(new WaitGroupId(user.getId(), group.getId()));
            groupuserRepository.save(new Groupuser(user, group));
            groupRecordRepository.save(new Grouprecord(group, executor, user, "accept"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean denyGroup(User user, User executor, Group group) {
        try {
            waitGroupRepository.deleteById(new WaitGroupId(user.getId(), group.getId()));
            groupRecordRepository.save(new Grouprecord(group, executor, user, "deny"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
