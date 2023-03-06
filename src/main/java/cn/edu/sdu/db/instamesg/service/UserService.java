package cn.edu.sdu.db.instamesg.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Boolean createUser(String username, String password, String email, MultipartFile avatar);
}
