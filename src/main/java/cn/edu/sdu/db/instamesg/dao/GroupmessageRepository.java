package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Friendmessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupmessageRepository extends JpaRepository<Groupmessage, Integer> {
    @Query("select f from Groupmessage f where f.group.id = ?1")
    List<Groupmessage> findByGroupId(Integer groupId);
}
