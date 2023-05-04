package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
    @Query("select f from Friend f where f.usera.id = ?1 or f.userb.id = ?1")
    List<Friend> findFriendsByUserid(Integer Userid);
}
