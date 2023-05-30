package cn.edu.sdu.db.instamesg.dao;

import cn.edu.sdu.db.instamesg.pojo.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select count(u) from User u where u.email = ?1")
    long countByEmail(String email);
    User findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);
    User findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.type = ?1 where u.id = ?2")
    void updateType(String type, Integer id);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?1 where u.id = ?2")
    void updatePassword(String hashedPassword, Integer id);

    @Transactional
    @Modifying
    @Query("update User u set u.email = ?1 where u.id = ?2")
    void updateEmail(String email, Integer id);

    @Transactional
    @Modifying
    @Query("update User u set u.registerTime = ?1 where u.id = ?2")
    void updateRegisterTime(Instant now, Integer id);

    @Transactional
    @Modifying
    @Query("update User u set u.portrait = ?1 where u.id = ?2")
    void updatePortrait(String path, Integer id);
}
