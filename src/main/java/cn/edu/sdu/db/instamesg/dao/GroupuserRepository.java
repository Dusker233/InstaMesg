package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Groupuser;
import cn.edu.sdu.db.instamesg.pojo.GroupuserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupuserRepository extends JpaRepository<Groupuser, GroupuserId> {
    @Query("select g from Groupuser g where g.id.userId = ?1 and g.id.groupId = ?2")
    Groupuser findByUseridAndGroupId(Integer userId, Integer groupId);

    @Modifying
    @Query("delete from Groupuser g where g.id.groupId = ?1")
    void deleteByGroupId(Integer groupId);
}
