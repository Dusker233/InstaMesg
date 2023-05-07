package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Banneduser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BanneduserRepository extends JpaRepository<Banneduser, Integer> {
    @Query("select b from Banneduser b where b.id.userId = ?1")
    Banneduser findByUserid(Integer id);
}
