package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.dao.*;
import cn.edu.sdu.db.instamesg.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupuserRepository groupuserRepository;

    @Autowired
    private WaitGroupRepository waitGroupRepository;

    @Autowired
    private GroupRecordRepository groupRecordRepository;

    @Autowired
    private GroupmanRepository groupmanRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public Boolean addGroup(User user, Group group, String reason) {
        try {
            if(groupuserRepository.findByUseridAndGroupId(user.getId(), group.getId()) != null)
                return false;
            waitGroupRepository.save(new WaitGroup(user, group, reason));
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

    @Override
    public Boolean deleteGroup(User user, Group group) {
        try {
            groupuserRepository.deleteById(new GroupuserId(group.getId(), user.getId()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean destroyGroup(User user, Group group) {
        try {
            groupuserRepository.deleteByGroupId(group.getId());
            groupmanRepository.deleteByGroupId(group.getId());
            groupRecordRepository.deleteByGroupId(group.getId());
            groupRepository.deleteById(group.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean revokeGroupManager(User user, User executor, Group group) {
        try {
            groupmanRepository.deleteByGroupIdAndUserId(group.getId(), user.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean setGroupMaster(User user, User executor, Group group) {
        try {
            groupmanRepository.save(new Groupman(new GroupmanId(group.getId(), user.getId()), "master"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean setGroupManager(User user, User executor, Group group) {
        try {
            groupmanRepository.save(new Groupman(new GroupmanId(group.getId(), user.getId()), "manager"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
