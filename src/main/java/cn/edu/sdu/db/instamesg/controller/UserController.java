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
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.sdu.db.instamesg.tools.getCilentIp.getIP;

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

    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> captchaMap = new HashMap<>();

    private static final String mailUserName = "instamesg@163.com";

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
            User user = userRepository.findById(userId).orElse(null);
            assert user != null;
            String otpUrl = OTP.getURL(user.getSecret(), 6, Type.TOTP, "InstaMesg-2FA", user.getUsername());
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
        if(username.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"))
            username = userRepository.findByEmail(username).getUsername();
//        System.out.println(username + " " + password + " " + User.getHashedPassword(password));
        User user = userService.getUser(username, password);
        if(user != null) {
            if(user.getType().equals("deleted") || user.getType().equals("banned"))
                return new SimpleResponse(false, "This user has been deleted or banned");
            String serverGeneratedCode = OTP.create(user.getSecret(), OTP.timeInHex(), 6, Type.TOTP);
//            System.out.println(serverGeneratedCode);
            if(!serverGeneratedCode.equals(authCode))
                return new SimpleResponse(false, "Wrong 2-FA code");
            session.setAttribute("user", user.getId());
            if(userService.addIP(user.getId(), getIP()))
                return new SimpleResponse(true, "");
            else {
                session.removeAttribute("user");
                return new SimpleResponse(false, "Can't get your IP address");
            }
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
        List<User> users = new ArrayList<>();
        // one-way friend relation
        for(Friend friend: FriendRelations) {
            if(friend.getUsera().getId() == userId)
                users.add(friend.getUserb());
        }
        if(users.size() == 0)
            return new DataResponse(false, "This user doesn't have friends", null);
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
                          @RequestParam(required = false) String type, @RequestParam(required = false) MultipartFile avatar) throws IOException {
    if(session.getAttribute("user") == null)
        return new SimpleResponse(false, "not logged in");
    int userId = (int)session.getAttribute("user");
    User user = userRepository.findByUsername(username);
    User admin = userRepository.findById(userId).orElse(null);
    if(user == null)
        return new SimpleResponse(false, "This user doesn't exist");
    // admin can modify other's information, including banned and deleted user
    if(!username.equals(user.getUsername())) {
        if(!admin.getType().equals("admin"))
            return new SimpleResponse(false, "You don't have the permission to modify other's information");
        else {
            if(password != null)
                userRepository.updatePassword(User.getHashedPassword(password), user.getId());
            if(type != null && (type.equals("admin") || type.equals("normal") || type.equals("deleted")))
                userRepository.updateType(type, user.getId());
            else if(type != null)
                return new SimpleResponse(false, "Wrong type");
            if(avatar != null) {
                userRepository.updatePortrait(avatar.getBytes(), user.getId());
            }
        }
    }
    else {
        if(password != null)
            userRepository.updatePassword(User.getHashedPassword(password), user.getId());
        if(type != null && !user.getType().equals("admin"))
            return new SimpleResponse(false, "You don't have the permission to modify your type");
        else if(user.getType().equals("admin")) {
            if(type != null && (type.equals("admin") || type.equals("normal") || type.equals("deleted")))
                userRepository.updateType(type, user.getId());
            else if(type != null)
                return new SimpleResponse(false, "Wrong type");
        }
        if(avatar != null) {
            userRepository.updatePortrait(avatar.getBytes(), user.getId());
        }
    }
    return new SimpleResponse(true, "");
    }

    /**
     * use {@code get} method to get a random code for reset password and 2FA
     * @param email email passed by the client
     * @return {@code ApiResponse} shows whether the operation is successful or not
     */
    @GetMapping("/getReset")
    public synchronized ApiResponse getReset(@RequestParam String email) {
        if(!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"))
            return new SimpleResponse(false, "Wrong email format");
        User user = userRepository.findByEmail(email);
        if(user == null)
            return new SimpleResponse(false, "This email doesn't exist");
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        if(!captchaMap.containsKey(email))
            captchaMap.put(email, verifyCode);
        else
            captchaMap.replace(email, verifyCode);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("您好：<br/>");
        stringBuilder.append("您的验证码是：").append(verifyCode).append("<br/>");
        stringBuilder.append("您可以复制此验证码并返回至InstaMesg，以验证您的邮箱。<br/>");
        stringBuilder.append("此验证码只能使用一次，在5分钟内有效。验证成功则自动失效。<br/>");
        stringBuilder.append("如果您没有进行上述操作，请忽略此邮件。");
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailUserName);
            helper.setTo(email);
            helper.setSubject("邮箱验证");
            helper.setText(stringBuilder.toString(), true);
            mailSender.send(message);
            return new SimpleResponse(true, verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new SimpleResponse(false, "sent failed");
        }
    }

    /**
     * use {@code post} method to reset password
     * @param email email passed by the client
     * @param password password passed by the client
     * @param captcha captcha passed by the client
     * @return {@code ApiResponse} shows whether the operation is successful or not
     */
    @PostMapping("/reset")
    public synchronized ApiResponse reset(@RequestParam String email, @RequestParam String password,
                                          @RequestParam String captcha) {
        if(!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"))
            return new SimpleResponse(false, "Wrong email format");
        User user = userRepository.findByEmail(email);
        if(user == null)
            return new SimpleResponse(false, "This email doesn't exist");
        if(!captchaMap.containsKey(email))
            return new SimpleResponse(false, "Please get captcha first");
        if(!captchaMap.get(email).equals(captcha))
            return new SimpleResponse(false, "Wrong captcha");
        userRepository.updatePassword(User.getHashedPassword(password), user.getId());
        return new SimpleResponse(true, "");
    }

    @PostMapping("/ban")
    public synchronized ApiResponse ban(HttpSession session, @RequestParam String username, @RequestParam(required = false) String reason) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "not logged in");
        int userId = (int)session.getAttribute("user");
        User admin = userRepository.findById(userId).orElse(null);
        assert admin != null;
        if(!admin.getType().equals("admin"))
            return new SimpleResponse(false, "You don't have the permission to ban user");
        else if(userService.banUser(admin, username, reason))
            return new SimpleResponse(true, "");
        else
            return new SimpleResponse(false, "Internal error");
    }

    @GetMapping("/info")
    public synchronized DataResponse info(HttpSession session) {
        if(session.getAttribute("user") == null)
            return new DataResponse(false, "not logged in", null);
        int userId = (int)session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        assert user != null;
        UserInfo userinfo = new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getType(), user.getRegisterTime());
        return new DataResponse(true, "", userinfo);
    }
}
