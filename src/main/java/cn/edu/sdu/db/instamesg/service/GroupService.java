package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.pojo.Group;
import cn.edu.sdu.db.instamesg.pojo.User;

public interface GroupService {
    Boolean addGroup(User user, Group group, String reason);

    Boolean acceptGroup(User user, User executor, Group group);

    Boolean denyGroup(User user, User executor, Group group);

    Boolean deleteGroup(User user, Group group);

    Boolean destroyGroup(User user, Group group);

    Boolean revokeGroupManager(User user, User executor, Group group);

    Boolean setGroupMaster(User user, User executor, Group group);

    Boolean setGroupManager(User user, User executor, Group group);
}
