/**
 * @author Dusker
 * @date 2023/03/09
 * @version 1.0
 */
package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.pojo.User;
import cn.edu.sdu.db.instamesg.tools.AvatarResize;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Get the user by username and password
     * @param username username passed by the client
     * @param password password passed by the client
     * @return {@code User} if the user exists, {@code null} if not
     * @since 1.0
     */
    @Override
    public User getUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, User.getHashedPassword(password));
    }

    /**
     * Create a new user
     * @param username username passed by the client
     * @param password password passed by the client
     * @param email email passed by the client
     * @param avatar avatar passed by the client, it can't be null
     * @return {@code 0} if the operation is successful, {@code 1} if the username already exists, {@code 2} if SQL error,
     *          {@code 3} if the password is shorter than 6 or not exists number and letter at the same time
     * @since 1.0
     */
    @Override
    public Integer createUser(String username, String password, String email, @NotNull MultipartFile avatar) {
        try {
            if(password.length() < 6 || !password.matches(".*[0-9].*") || !password.matches(".*[a-zA-Z].*"))
                return 3;
            AvatarResize avatarResize = new AvatarResize();
            MultipartFile Avatar = avatarResize.resize(avatar);
            // Check if exists a user whose username equals input and type equals "deleted"
            User prevUser = userRepository.findByUsername(username);
            if(prevUser != null) {
                if(prevUser.getType().equals("deleted")) {
                    userRepository.updateType("normal", prevUser.getId());
                    userRepository.updatePassword(User.getHashedPassword(password), prevUser.getId());
                    userRepository.updateEmail(email, prevUser.getId());
                    userRepository.updateRegisterTime(Instant.now(), prevUser.getId());
                    userRepository.updatePortrait(avatar.getBytes(), prevUser.getId());
                    return 0;
                }
                else {
                    return 1;
                }
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setRegisterTime(Instant.now());
            user.setPortrait(Avatar.getBytes());
            user.setType("normal");
            userRepository.save(user);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
}
