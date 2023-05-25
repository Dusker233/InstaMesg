package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Grouprecord;
import cn.edu.sdu.db.instamesg.pojo.GrouprecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupRecordRepository extends JpaRepository<Grouprecord, GrouprecordId> {
    @Modifying
    @Query("delete from Grouprecord g where g.id.groupId = ?1")
    void deleteByGroupId(Integer id);
}
