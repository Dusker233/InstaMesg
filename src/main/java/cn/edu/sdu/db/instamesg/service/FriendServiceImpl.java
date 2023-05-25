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


    @Override
    public Boolean addFriend(User user, User friend, String reason) {
        try {
            Friend relationship = friendRepository.findById(new FriendId(user.getId(), friend.getId())).orElse(null);
            if(relationship != null)
                return false;
            waitUserRepository.save(new WaitUser(user, friend, reason));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean acceptFriend(User user, User friend) {
        try {
            waitUserRepository.deleteById(new WaitUserId(friend.getId(), user.getId()));
            if(waitUserRepository.findById(new WaitUserId(user.getId(), friend.getId())).orElse(null) != null)
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
            waitUserRepository.deleteById(new WaitUserId(friend.getId(), user.getId()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean deleteFriend(User fromUser, User toUser) {
        try {
            friendRepository.deleteById(new FriendId(fromUser.getId(), toUser.getId()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
