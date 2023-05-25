package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Groupman;
import cn.edu.sdu.db.instamesg.pojo.GroupmanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupmanRepository extends JpaRepository<Groupman, GroupmanId> {
    @Query("select g from Groupman g where g.id.groupId = ?1 and g.id.managerId = ?2")
    Groupman findByGroupidAndUserid(Integer groupid, Integer userid);

    @Modifying
    @Query("delete from Groupman g where g.id.groupId = ?1")
    void deleteByGroupId(Integer id);

    @Modifying
    @Query("delete from Groupman g where g.id.groupId = ?1 and g.id.managerId = ?2")
    void deleteByGroupIdAndUserId(Integer groupid, Integer managerid);

    @Query("select g from Groupman g where g.id.managerId = ?1")
    List<Groupman> findAllByUserid(int userId);
}
