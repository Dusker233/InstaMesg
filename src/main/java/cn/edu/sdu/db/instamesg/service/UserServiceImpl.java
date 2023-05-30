/**
 * @author Dusker
 * @date 2023/03/09
 * @version 1.0
 */
package cn.edu.sdu.db.instamesg.service;

import cn.edu.sdu.db.instamesg.controller.FileController;
import cn.edu.sdu.db.instamesg.dao.BanneduserRepository;
import cn.edu.sdu.db.instamesg.dao.IpLocationRepository;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.pojo.*;
import cn.edu.sdu.db.instamesg.tools.AvatarResize;
import cn.edu.sdu.db.instamesg.tools.ImageUtils;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.amdelamar.jotp.OTP;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IpLocationRepository ipLocationRepository;

    @Autowired
    private BanneduserRepository banneduserRepository;

    @Autowired
    private FileController fileController;

    private ImageUtils imageUtils;

    /**
     * Get the user by username and password
     * @param username username passed by the client
     * @param password password passed by the client
     * @return {@code User} if the user exists, {@code null} if not
     * @since 1.0
     */
    @Override
    public synchronized User getUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, User.getHashedPassword(password));
    }

    /**
     * Create a new user
     * @param username username passed by the client
     * @param password password passed by the client
     * @param email email passed by the client
     * @param avatar avatar passed by the client, it can't be null
     * @return {@code 0} if the operation is successful, {@code 1} if the username already exists, {@code 2} if SQL error,
     *          {@code 3} if the password is shorter than 6 or longer than 24 or not exists number and letter at the same time
     * @since 1.0
     */
    @Override
    public synchronized Integer createUser(String username, String password, String email, @NotNull MultipartFile avatar) {
        try {
            if(password.length() < 6 || !password.matches(".*[0-9].*") || !password.matches(".*[a-zA-Z].*") || password.length() > 24)
                return 3;
            AvatarResize avatarResize = new AvatarResize();
            MultipartFile Avatar = avatarResize.resize(avatar);
            String path = null;
//            System.out.println(avatar.getName());
            if(avatar.getName().equals("defaultAvatar"))
                path = "http://124.70.165.173:11451/instamesg/avatar/defaultAvatar.png";
            else
                path = fileController.uploadAvatar(Avatar, username);
            if(path.equals("error"))
                return 2;
            // Check if exists a user whose username equals input and type equals "deleted"
            User prevUser = userRepository.findByUsername(username);
            if(prevUser != null) {
                if(prevUser.getType().equals("deleted")) {
                    userRepository.updateType("normal", prevUser.getId());
                    userRepository.updatePassword(User.getHashedPassword(password), prevUser.getId());
                    userRepository.updateEmail(email, prevUser.getId());
                    userRepository.updateRegisterTime(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))), prevUser.getId());
                    userRepository.updatePortrait(path, prevUser.getId());
                    return 0;
                }
                else {
                    return 1;
                }
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setSecret(OTP.randomBase32(64));
            user.setEmail(email);
            user.setRegisterTime(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))));
            user.setPortrait(path);
            user.setType("normal");
            userRepository.save(user);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    @Override
    public synchronized Boolean addIP(Integer id, String ip) {
        if(ip.equals("Can't get IP"))
            return false;
        IpLocation ipLocation = new IpLocation();
        IpLocationId ipLocationId = new IpLocationId();
        ipLocationId.setUserId(id);
        ipLocationId.setLoginTime(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))));
        ipLocation.setId(ipLocationId);
        ipLocation.setIp(ip);
        ipLocationRepository.save(ipLocation);
        return true;
    }

    @Override
    public synchronized Boolean banUser(User admin, String username, String reason) {
        try {
            User user = userRepository.findByUsername(username);
            if(user == null)
                return false;
            if(user.getType().equals("banned"))
                return false;
            Banneduser banneduser = new Banneduser();
            BanneduserId banneduserId = new BanneduserId();
            banneduserId.setUserId(userRepository.findByUsername(username).getId());
            banneduserId.setBanTime(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))));
            banneduser.setId(banneduserId);
            banneduser.setExecutor(admin.getId());
            if(reason != null)
                banneduser.setBanReason(reason);
            else
                banneduser.setBanReason("No reason");
            banneduserRepository.save(banneduser);
            userRepository.updateType("banned", banneduserId.getUserId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean unbanUser(User admin, String username) {
        try {
            User user = userRepository.findByUsername(username);
            if(user == null)
                return false;
            if(!user.getType().equals("banned"))
                return false;
            Banneduser banneduser = banneduserRepository.findByUserid(user.getId());
            banneduserRepository.delete(banneduser);
            userRepository.updateType("normal", user.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
