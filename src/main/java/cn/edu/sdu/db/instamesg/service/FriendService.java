package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.pojo.User;

public interface FriendService {
    Boolean addFriend(User user, User friend, String reason);

    Boolean acceptFriend(User user, User friend);

    Boolean denyFriend(User user, User friend);

    Boolean deleteFriend(User fromUser, User toUser);
}
