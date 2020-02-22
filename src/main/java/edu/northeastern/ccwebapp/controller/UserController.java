package edu.northeastern.ccwebapp.controller;

import com.timgroup.statsd.StatsDClient;
import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    private final StatsDClient stats;

    private final UserService userService;
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, StatsDClient stats) {
        this.userService = userService;
        this.stats = stats;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity basicAuth(HttpServletRequest req) {
        stats.incrementCounter("endpoint.getuser.http.get");
        String headerResp = req.getHeader("Authorization");
        logger.info("You are in get user controller api");
        return userService.checkUserStatus(headerResp);
    }

    @PostMapping(value = "/user/register", produces = "application/json", consumes = "application/json")
    public ResponseEntity registerUser(@RequestBody User user) {
        stats.incrementCounter("endpoint.createuser.http.post");
        logger.info("You are in post user controller api");
        return userService.saveUser(user);
    }

    @GetMapping(value = "/test")
    public String test(HttpServletRequest req) {
        return "Test succeed!";
    }

    @PostMapping(value = "/reset", produces = "application/json", consumes = "application/json")
    public ResponseEntity resetPassword(@RequestBody User user) {
        stats.incrementCounter("endpoint.user.password.reset");
        logger.info("In user controller reset password method");
        return userService.resetPassword(user);
    }
}
