package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.WaitGroup;
import cn.edu.sdu.db.instamesg.pojo.WaitGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitGroupRepository extends JpaRepository<WaitGroup, WaitGroupId> {
}
