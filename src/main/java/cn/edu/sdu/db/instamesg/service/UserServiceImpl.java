package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public Boolean createUser(String username, String password, String email, MultipartFile avatar) {
        try {
            // Check if exists a user whose username equals input and type equals "deleted"
            User prevUser = userRepository.findByUsername(username);
            if(prevUser != null) {
                if(prevUser.getType().equals("deleted")) {
                    userRepository.updateType("normal", prevUser.getId());
                    userRepository.updatePassword(User.getHashedPassword(password), prevUser.getId());
                    userRepository.updateEmail(email, prevUser.getId());
                    userRepository.updateRegisterTime(Instant.now(), prevUser.getId());
                    userRepository.updatePortrait(avatar.getBytes(), prevUser.getId());
                    return true;
                }
                else {
                    return false;
                }
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setRegisterTime(Instant.now());
            user.setPortrait(avatar.getBytes());
            user.setType("normal");
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
