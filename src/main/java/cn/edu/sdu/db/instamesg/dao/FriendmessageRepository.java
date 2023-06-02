package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Friendmessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendmessageRepository extends JpaRepository<Friendmessage, Integer> {
    @Query("select f from Friendmessage f where f.sender.id = ?1 and f.receiver.id = ?2")
    List<Friendmessage> findBySenderIdAndReceiverId(Integer id, Integer id1);
}
