package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Group;
import cn.edu.sdu.db.instamesg.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    Group findGroupById(Integer id);

    Group findGroupByGroupName(String groupName);

    @Query("select count(*) from Groupuser g where g.id.userId = ?1 and g.id.groupId = ?2")
    Boolean findByUserIdAndGroupId(Integer userid, Integer groupid);

}