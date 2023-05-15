package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Groupman;
import cn.edu.sdu.db.instamesg.pojo.GroupmanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupmanRepository extends JpaRepository<Groupman, GroupmanId> {
    @Query("select g from Groupman g where g.id.groupId = ?1 and g.id.managerId = ?2")
    Groupman findByGroupidAndUserid(Integer groupid, Integer userid);
}
