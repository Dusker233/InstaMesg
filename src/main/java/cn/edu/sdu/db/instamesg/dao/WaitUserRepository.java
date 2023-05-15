package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.WaitUser;
import cn.edu.sdu.db.instamesg.pojo.WaitUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WaitUserRepository extends JpaRepository<WaitUser, WaitUserId> {
    @Query("select w from WaitUser w where w.id.userToId = ?1")
    List<WaitUser> findAllByToId(Integer id);

    @Query("select w from WaitUser w where w.id.userFromId = ?1")
    List<WaitUser> findAllByFromId(Integer id);
}