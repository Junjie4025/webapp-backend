package edu.northeastern.ccwebapp.controller;

import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity basicAuth(HttpServletRequest req) {
        String headerResp = req.getHeader("Authorization");
        return userService.checkUserStatus(headerResp);
    }

    @PostMapping(value = "/user/register", produces = "application/json", consumes = "application/json")
    public ResponseEntity registerUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
