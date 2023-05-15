package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.pojo.Group;
import cn.edu.sdu.db.instamesg.pojo.User;

public interface FriendService {
    Boolean addFriend(User user, User friend, String reason);

    Boolean addGroup(User user, Group group, String reason);

    Boolean acceptFriend(User user, User friend);

    Boolean denyFriend(User user, User friend);

    Boolean acceptGroup(User user, User executor, Group group);

    Boolean denyGroup(User user, User executor, Group group);
}
