package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.Banneduser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanneduserRepository extends JpaRepository<Banneduser, Integer> {
}
