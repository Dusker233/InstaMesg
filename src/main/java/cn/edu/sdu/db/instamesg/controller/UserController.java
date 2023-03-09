/**
 * @author Dusker
 * @date 2023/03/09
 * @version 1.0
 */
package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.ApiResponse;
import cn.edu.sdu.db.instamesg.api.SimpleResponse;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.filter.CORSFilter;
import cn.edu.sdu.db.instamesg.pojo.User;
import cn.edu.sdu.db.instamesg.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

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
    public ApiResponse register(@RequestParam String username, @RequestParam String password,
                                @RequestParam String email, @RequestParam(required = false) MultipartFile avatar)
                throws FileNotFoundException, IOException {

        if(!(avatar == null)) {
            if(!(avatar.getContentType().contains("image")))
                return new SimpleResponse(false, "Not an image");
            int returnCode = userService.createUser(username, password, email, avatar);
            if(returnCode == 0)
                return new SimpleResponse(true, "");
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
            if(returnCode == 0)
                return new SimpleResponse(true, "");
            else if(returnCode == 1)
                return new SimpleResponse(false, "Already exists a same username");
            else if(returnCode == 2)
                return new SimpleResponse(false, "SQL error");
            else
                return new SimpleResponse(false, "Password is shorter than 6 or not exists number and letter at the same time");
        }
    }

    /**
     * Using {@code post} method to login
     * @param username username passed by the client
     * @param password password passed by the client
     * @param session {@code HttpSession} passed by the client
     * @return {@code ApiResponse} shows whether the operation is successful or not
     * @since 1.0
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestParam String username, @RequestParam String password, HttpSession session) {
    if(session.getAttribute("user") != null) {
        return new SimpleResponse(false, "You've already logged in");
    }
    User user = userService.getUser(username, password);
    if(user != null) {
        if(user.getType().equals("deleted"))
            return new SimpleResponse(false, "This user has been deleted");
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
    public ApiResponse logout(HttpSession session) {
        if (session.getAttribute("user") != null) {
            session.removeAttribute("user");
            return new SimpleResponse(true, "");
        } else {
            return new SimpleResponse(false, "Not logged in");
        }
    }
}
