package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Grouprecord;
import cn.edu.sdu.db.instamesg.pojo.GrouprecordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRecordRepository extends JpaRepository<Grouprecord, GrouprecordId> {
}
