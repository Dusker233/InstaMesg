package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Groupuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupuserRepository extends JpaRepository<Groupuser, Integer> {
    @Query("select g from Groupuser g where g.id.userId = ?1 and g.id.groupId = ?2")
    Groupuser findByUseridAndGroupid(Integer userId, Integer groupId);
}
