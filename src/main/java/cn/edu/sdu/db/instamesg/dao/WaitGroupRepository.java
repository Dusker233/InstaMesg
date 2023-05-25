package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.WaitGroup;
import cn.edu.sdu.db.instamesg.pojo.WaitGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface WaitGroupRepository extends JpaRepository<WaitGroup, WaitGroupId> {
    @Query("select w from WaitGroup w where w.id.userId = ?1")
    List<WaitGroup> findAllByUserId(Integer id);

    @Query("select w from WaitGroup w where w.id.groupId = ?1")
    List<WaitGroup> findAllByGroupId(Integer groupId);
}
