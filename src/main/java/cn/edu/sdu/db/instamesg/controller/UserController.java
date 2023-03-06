package cn.edu.sdu.db.instamesg.controller;

import cn.edu.sdu.db.instamesg.api.ApiResponse;
import cn.edu.sdu.db.instamesg.api.SimpleResponse;
import cn.edu.sdu.db.instamesg.dao.UserRepository;
import cn.edu.sdu.db.instamesg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse register(@RequestParam String username, @RequestParam String password,
                                @RequestParam String email, @RequestParam(required = false) MultipartFile avatar) {

        if(!(avatar == null)) {
            if(userService.createUser(username, password, email, avatar))
                return new SimpleResponse(true, "");
            else
                return new SimpleResponse(false, "Server error");
        } else {
            return new SimpleResponse(false, "No avatar uploaded");
        }
    }
}
