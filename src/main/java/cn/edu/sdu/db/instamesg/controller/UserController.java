/**
 * @author Dusker
 * @date 2023/05/04
 * @version 1.1
 */
package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.ApiResponse;
import cn.edu.sdu.db.instamesg.api.DataResponse;
import cn.edu.sdu.db.instamesg.api.SimpleResponse;
import cn.edu.sdu.db.instamesg.api.UserInfo;
import cn.edu.sdu.db.instamesg.dao.FriendRepository;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.filter.CORSFilter;
import cn.edu.sdu.db.instamesg.pojo.Friend;
import cn.edu.sdu.db.instamesg.pojo.User;
import cn.edu.sdu.db.instamesg.service.UserService;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Slf4j0
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CORSFilter corsFilter;

    /**
     * Using {@code post} method to register a new user
     * @param username username passed by the client
     * @param password password passed by the client
     * @param email email passed by the client
     * @param avatar avatar passed by the client, it can be null
     * @return {@code ApiResponse} shows whether the operation is successful or not
     * @throws FileNotFoundException if the default avatar is not found
     * @throws IOException if the default avatar is not found
     * @since 1.0
     */
    @PostMapping("/register")
    public synchronized ApiResponse register(@RequestParam String username, @RequestParam String password,
                                @RequestParam String email, @RequestParam(required = false) MultipartFile avatar, HttpSession session)
                throws FileNotFoundException, IOException {

        if(avatar != null) {
            if(!(avatar.getContentType().contains("image")))
                return new SimpleResponse(false, "Not an image");
            int returnCode = userService.createUser(username, password, email, avatar);
            if(returnCode == 0) {
                int userid = userRepository.findByUsername(username).getId();
                session.setAttribute("user", userid);
                return new SimpleResponse(true, "");
            }
            else if(returnCode == 1)
                return new SimpleResponse(false, "Already exists a same username");
            else if(returnCode == 2)
                return new SimpleResponse(false, "SQL error");
            else
                return new SimpleResponse(false, "Password is shorter than 6 or not exists number and letter at the same time");
        } else {
            File file = new File("./defaultAvatar.png");
            FileInputStream fileInputStream = new FileInputStream(file);
            MultipartFile Avatar = new MockMultipartFile("defaultAvatar", fileInputStream);
            int returnCode = userService.createUser(username, password, email, Avatar);
            if(returnCode == 0) {
                int userid = userRepository.findByUsername(username).getId();
                session.setAttribute("user", userid);
                return new SimpleResponse(true, "");
            }
            else if(returnCode == 1)
                return new SimpleResponse(false, "Already exists a same username");
            else if(returnCode == 2)
                return new SimpleResponse(false, "SQL error");
            else
                return new SimpleResponse(false, "Password is shorter than 6 or not exists number and letter at the same time");
        }
    }

    /**
     * Using {@code get} method to give new registered user a QR code for 2-FA authentication initialization
     * @param session {@code HttpSession} passed by {@code register} function
     * @return {@code ApiResponse} shows whether the operation is successful or not
     * @since 1.1
     */

    @GetMapping("/registered")
    public synchronized ApiResponse twoFA(HttpSession session) {
        if(session.getAttribute("user") == null) {
            return new SimpleResponse(false, "redirect:/user/register");
        } else {
            int userId = (int) session.getAttribute("user");
            User user = userRepository.findById(userId).get();
            String otpUrl = OTP.getURL(user.getSecret(), 6, Type.TOTP, "spring-boot-2FA", user.getUsername());
            String twoFaQrUrl = String.format(
                    "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=%s",
                    URLEncoder.encode(otpUrl, StandardCharsets.UTF_8));
            session.removeAttribute("user");
            return new SimpleResponse(true, twoFaQrUrl);
        }
    }

    /**
     * Using {@code post} method to login, where also contains 2-FA authentication
     * @param username username passed by the client
     * @param password password passed by the client
     * @param authCode 2-FA authentication code passed by the client
     * @param session {@code HttpSession} passed by the client
     * @return {@code ApiResponse} shows whether the operation is successful or not
     * @since 1.1
     */
    @PostMapping("/login")
    public synchronized ApiResponse login(@RequestParam String username, @RequestParam String password,
                                          @RequestParam String authCode, HttpSession session)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if(session.getAttribute("user") != null) {
            return new SimpleResponse(false, "You've already logged in");
        }
        if(username.contains("@"))
            username = userRepository.findByEmail(username).getUsername();
//        System.out.println(username + " " + password + " " + User.getHashedPassword(password));
        User user = userService.getUser(username, password);
        if(user != null) {
            if(user.getType().equals("deleted"))
                return new SimpleResponse(false, "This user has been deleted");
            String serverGeneratedCode = OTP.create(user.getSecret(), OTP.timeInHex(), 6, Type.TOTP);
//            System.out.println(serverGeneratedCode);
            if(!serverGeneratedCode.equals(authCode))
                return new SimpleResponse(false, "Wrong 2-FA code");
            session.setAttribute("user", user.getId());
            return new SimpleResponse(true, "");
        } else
            return new SimpleResponse(false, "Wrong username or password");
    }

    /**
     * Using {@code get} method to logout
     * @param session {@code HttpSession} passed by the client
     * @return {@code ApiResponse} shows whether the operation is successful or not
     * @since 1.0
     */
    @GetMapping("/logout")
    public synchronized ApiResponse logout(HttpSession session) {
        if (session.getAttribute("user") != null) {
            session.removeAttribute("user");
            return new SimpleResponse(true, "");
        } else
            return new SimpleResponse(false, "Not logged in");
    }

    /**
     * use {@code get} method to get user's friends‘ information
     * @param session {@code HttpSession} passed by the client
     * @return {@code DataResponse} shows whether current user has friends or not, or not logged in
     */
    @GetMapping("/list")
    public synchronized DataResponse list(HttpSession session) {
       if(session.getAttribute("user") == null)
           return new DataResponse(false, "not logged in", null);
       int userId = (int)session.getAttribute("user");
//        System.out.println(userId);
        List<Friend> FriendRelations = friendRepository.findFriendsByUserid(userId);
        if(FriendRelations.size() == 0)
            return new DataResponse(false, "This user doesn't have friends", null);
        List<User> users = new ArrayList<>();
        for(Friend friend: FriendRelations) {
            if(friend.getUsera().getId() == userId)
                users.add(friend.getUserb());
            else
                users.add(friend.getUsera());
        }
        List<UserInfo> results = users.stream().map((u)-> new UserInfo(u.getId(), u.getUsername(),
                u.getEmail(), u.getType(), u.getRegisterTime())).collect(Collectors.toList());
        return new DataResponse(true, "", results);
    }

    /**
     * use {@code post} method to modify user's information
     * @param session {@code HttpSession} passed by the client
     * @param username username passed by the client
     * @param password password passed by the client, it can be null
     * @param type type passed by the client, it can be null
     * @param avatar avatar passed by the client, it can be null
     * @return {@code ApiResponse} shows whether the operation is successful or not
     */
    @PostMapping("/modify")
    public synchronized ApiResponse modify(HttpSession session, @RequestParam String username, @RequestParam(required = false) String password,
                          @RequestParam(required = false) String type, @RequestParam(required = false) MultipartFile avatar) {
    if(session.getAttribute("user") == null)
        return new SimpleResponse(false, "not logged in");
    int userId = (int)session.getAttribute("user");
    return null;
    }
}
