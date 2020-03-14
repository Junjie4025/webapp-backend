package edu.northeastern.ccwebapp.controller;

import com.timgroup.statsd.StatsDClient;
import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    private final StatsDClient stats;

    private static final Counter defaultCounter = Metrics.counter("default_api_counter");
    private static final Counter userRegisterCounter = Metrics.counter("user_register_api_counter");
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
        this.stats = stats;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity basicAuth(HttpServletRequest req) {
        defaultCounter.increment();
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


    @PostMapping(value = "/user/register", produces = "application/json", consumes = "application/json")
    public ResponseEntity registerUser(@RequestBody User user) {
        userRegisterCounter.increment();
        logger.info("You are in post user controller api");
        return userService.saveUser(user);
    }
}
