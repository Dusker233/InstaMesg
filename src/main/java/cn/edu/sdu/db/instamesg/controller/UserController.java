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
import cn.edu.sdu.db.instamesg.dao.BanneduserRepository;
import cn.edu.sdu.db.instamesg.dao.FriendRepository;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.filter.CORSFilter;
import cn.edu.sdu.db.instamesg.pojo.Banneduser;
import cn.edu.sdu.db.instamesg.pojo.Friend;
import cn.edu.sdu.db.instamesg.pojo.User;
import cn.edu.sdu.db.instamesg.service.UserService;
import cn.edu.sdu.db.instamesg.tools.ImageUtils;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.sdu.db.instamesg.tools.get2FAUrl.get2FAUrl;
import static cn.edu.sdu.db.instamesg.tools.getClientIp.getIP;

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

    @Autowired
    private FileController fileController;

    private Map<String, String> captchaMap = new HashMap<>();
    private Map<String, Instant> timeMap = new HashMap<>();

    private static final String mailUserName = "instamesg@163.com";
    @Autowired
    private BanneduserRepository banneduserRepository;

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
        if(userRepository.countByEmail(email) > 0)
            return new SimpleResponse(false, "This email has been registered");
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
            String twoFaQrUrl = get2FAUrl(User.getHashedPassword(user.getSecret()), user.getUsername());
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
                                          @RequestParam String authCode, HttpSession session, HttpServletRequest request)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if(session.getAttribute("user") != null) {
            return new SimpleResponse(false, "You've already logged in");
        }
        if(username.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"))
            username = userRepository.findByEmail(username).getUsername();
//        System.out.println(username + " " + password + " " + User.getHashedPassword(password));
        User user = userService.getUser(username, password);
        if(user != null) {
            if(user.getType().equals("deleted"))
                return new SimpleResponse(false, "This user has been deleted");
            if(user.getType().equals("banned")) {
                Banneduser banneduser = banneduserRepository.findByUserid(user.getId());
                User executor = userRepository.findById(banneduser.getExecutor()).orElse(null);
                String reason = banneduser.getBanReason();
                Instant time = banneduser.getId().getBanTime();
                return new SimpleResponse(false, "You've been banned by " + executor.getUsername() + " for " + reason + " when " + time);
            }
            String serverGeneratedCode = OTP.create(User.getHashedPassword(user.getSecret()), OTP.timeInHex(System.currentTimeMillis()), 6, Type.TOTP);
            System.out.println(serverGeneratedCode);
            authCode = authCode.trim();
            if(!serverGeneratedCode.equals(authCode))
                return new SimpleResponse(false, "Wrong 2-FA code");
            session.setAttribute("user", user.getId());
            if(userService.addIP(user.getId(), getIP(request))) {
                session.setAttribute("user", user.getId());
                return new DataResponse(true, "", user.getId());
            }
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
        List<Friend> FriendRelations = friendRepository.findFriendsByUserid(userId);
        List<User> users = new ArrayList<>();
        // one-way friend relation
        for(Friend friend: FriendRelations) {
            if(friend.getId().getUseraId() == userId) {
                int userbId = friend.getId().getUserbId();
                User user = userRepository.findById(userbId).orElse(null);
                assert user != null;
                users.add(user);
            }
        }
        if(users.isEmpty())
            return new DataResponse(false, "This user doesn't have friends", null);
        List<UserInfo> results = users.stream().map((u)-> new UserInfo(u.getId(), u.getUsername(),
                u.getEmail(), u.getType(), u.getPortrait(), u.getRegisterTime())).collect(Collectors.toList());
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
    // admin can modify other's information, including deleted user
    if(!username.equals(admin.getUsername())) {
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
                String path = fileController.uploadAvatar(avatar, user.getUsername());
                if(path.equals("error"))
                    return new SimpleResponse(false, "Can't upload avatar");
                userRepository.updatePortrait(path, user.getId());
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
            String path = fileController.uploadAvatar(avatar, user.getUsername());
            if(path.equals("error"))
                return new SimpleResponse(false, "Can't upload avatar");
            userRepository.updatePortrait(path, user.getId());
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
        if(!captchaMap.containsKey(email)) {
            captchaMap.put(email, verifyCode);
            timeMap.put(email, Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))));
        }
        else {
            captchaMap.replace(email, verifyCode);
            timeMap.replace(email, Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("您好：<br/>");
        stringBuilder.append("您的验证码是：")
                .append(verifyCode)
                .append("<br/>");
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
     * use {@code post} method to reset password and 2FA
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
        if(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(8))).isAfter(timeMap.get(email).plus(Duration.ofMinutes(5))))
            return new SimpleResponse(false, "Captcha expired");
        userRepository.updatePassword(User.getHashedPassword(password), user.getId());
        String twoFaUrl = get2FAUrl(User.getHashedPassword(user.getSecret()), user.getUsername());
        return new SimpleResponse(true, twoFaUrl);
    }

    /**
     * use {@code post} method to ban a user
     * @param session session passed by the client
     * @param username username passed by the client, showing who is going to be banned
     * @param reason reason passed by the client, showing why this user is banned
     * @return {@code ApiResponse} shows whether the operation is successful or not
     */
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
            return new SimpleResponse(false, "Can't find this user or this user is already banned");
    }

    /**
     * use {@code post} method to unban a user
     * @param session session passed by the client
     * @param username username passed by the client, showing who is going to be unbanned
     * @return {@code ApiResponse} shows whether the operation is successful or not
     */
    @PostMapping("/unban")
    public synchronized ApiResponse unban(HttpSession session, @RequestParam String username) {
        if(session.getAttribute("user") == null)
            return new SimpleResponse(false, "not logged in");
        int userId = (int)session.getAttribute("user");
        User admin = userRepository.findById(userId).orElse(null);
        assert admin != null;
        if(!admin.getType().equals("admin"))
            return new SimpleResponse(false, "You don't have the permission to unban user");
        else if(userService.unbanUser(admin, username))
            return new SimpleResponse(true, "");
        else
            return new SimpleResponse(false, "Can't find this user or this user is not banned");
    }

    /**
     * use {@code get} method to get user himself info
     * @param session session passed by the client
     * @return {@code DataResponse} shows whether the operation is successful or not
     */
    @GetMapping("/info")
    public synchronized DataResponse info(HttpSession session) {
        if(session.getAttribute("user") == null)
            return new DataResponse(false, "not logged in", null);
        int userId = (int)session.getAttribute("user");
        User user = userRepository.findById(userId).orElse(null);
        assert user != null;
        UserInfo userinfo = new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getType(), user.getPortrait(), user.getRegisterTime());
        return new DataResponse(true, "", userinfo);
    }

    /**
     * use {@code get} method to get user info by username
     * @param username username passed by the client
     * @return {@code DataResponse} shows whether the operation is successful or not
     */
    @GetMapping("/info/{username}")
    public synchronized DataResponse info(@PathVariable String username, HttpSession session) {
        if(session.getAttribute("user") == null)
            return new DataResponse(false, "not logged in", null);
        User user = userRepository.findByUsername(username);
        if(user == null)
            return new DataResponse(false, "Can't find this user", null);
        UserInfo userinfo = new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getType(), user.getPortrait(), user.getRegisterTime());
        return new DataResponse(true, "", userinfo);
    }
}

