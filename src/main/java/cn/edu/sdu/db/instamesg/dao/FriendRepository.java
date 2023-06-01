package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Friend;
import cn.edu.sdu.db.instamesg.pojo.FriendId;
import cn.edu.sdu.db.instamesg.pojo.Group;
import cn.edu.sdu.db.instamesg.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {
    @Query("select f from Friend f where f.id.useraId = ?1 or f.id.userbId = ?1")
    List<Friend> findFriendsByUserid(Integer Userid);

    @Query("select f from Friend f where f.id.useraId = ?1 and f.id.userbId = ?2")
    Friend findByUserAIdandUserBId(Integer userAId, Integer userBId);

    @Query("select f from Friend f where f.id.useraId = ?1")
    List<Friend> findAllByUserAId(int userId);
}
