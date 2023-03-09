package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.pojo.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Integer createUser(String username, String password, String email, MultipartFile avatar);

    User getUser(String username, String password);
}
