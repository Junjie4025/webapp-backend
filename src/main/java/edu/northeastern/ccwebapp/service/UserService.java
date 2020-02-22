package edu.northeastern.ccwebapp.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import edu.northeastern.ccwebapp.Util.ResponseMessage;
import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final static Logger logger = LogManager.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity checkUserStatus(String headerResp) {
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity message;

        if (headerResp != null && headerResp.contains("Basic")) {
            String[] userDetails = new String(Base64.getDecoder().decode(headerResp.substring(6).getBytes())).split(":", 2);//decode the header and split into username and password
            User user = findByUserName(userDetails[0]);//find it by username
            if (user != null) {
                if (new BCryptPasswordEncoder().matches(userDetails[1], user.getPassword())) {//check for password match
                    responseMessage.setMessage("Current time - " + new Date());
                    logger.info("Current time - " + new Date());
                    message = new ResponseEntity<>(responseMessage, HttpStatus.OK);

                } else {
                    logger.warn("Please enter valid credentials");
                    responseMessage.setMessage("Please enter valid credentials");
                    message = new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
                }
            } else {
                logger.info("User does not exist");
                responseMessage.setMessage("User does not exist");
                message = new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
            }
        } else {
            logger.info("User is not logged in");
            responseMessage.setMessage("User is not logged in");
            message = new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
        }
        return message;
    }

    private String validateUser(User user) {

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            logger.error("username is empty or json format is not correct");
            return "username is empty or json format is not correct";
        } else if (user.getPassword() == null || user.getPassword().isEmpty()) {
            logger.error("password is empty or json format is not correct");
            return "password is empty or json format is not correct";
        }

        String regExpression = "^([a-zA-Z0-9_.+-])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        Pattern pattern = Pattern.compile(regExpression);
        Matcher matcher = pattern.matcher(user.getUsername());

        if (!matcher.matches()) {
            logger.error("Please enter a valid email address.");
            return "Please enter a valid email address.";
        }

        regExpression = "^[a-zA-Z0-9!$#@%^&_*]\\w{7,18}$";
        //"^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d-]{8,}$";
        /*The password's first character must be a letter, it must contain at least 8 characters and
        no more than 15 characters and no characters other than letters,
        numbers and the underscore may be used*/

        pattern = Pattern.compile(regExpression);
        matcher = pattern.matcher(user.getPassword());

        if (!matcher.matches()) {
            logger.error("Please enter a valid password of minimum length 8 characters");
            return "Please enter a valid password of minimum length 8 characters";
        }
        return "success";
    }

    public ResponseEntity saveUser(User user) {
        ResponseMessage errorMessage = new ResponseMessage();
        String responseMessage = validateUser(user);

        if (responseMessage.equalsIgnoreCase("success")) {
            User ud = new User();
            ud.setUsername(user.getUsername());
            ud.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            User userByUsername = this.findByUserName(user.getUsername());
            if (userByUsername != null) {
                logger.warn("Username already exists!");
                errorMessage.setMessage("Username already exists!");
                return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
            }
            userRepository.save(ud);
            logger.info("User registered successfully.");
            errorMessage.setMessage("User registered successfully.");
            return new ResponseEntity<>(errorMessage, HttpStatus.OK);

        } else {
            errorMessage.setMessage(responseMessage);
            logger.error("Could not register, bad request");
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public ResponseEntity resultOfUserStatus(HttpServletRequest request) {
        String headerResp = request.getHeader("Authorization");
        return this.checkUserStatus(headerResp);
    }

    public ResponseEntity resetPassword(User user) {
        ResponseMessage msg = new ResponseMessage();
        if (findByUserName(user.getUsername()) != null) {
            AmazonSNS sns = AmazonSNSClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain()).build();
            sns.publish(new PublishRequest(sns.createTopic("reset_password").getTopicArn(), "{ \"email\":\"" + user.getUsername() + "\"}"));
            logger.info("Published message to SNS");
            //msg.setMessage("Email sent successfully");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            logger.info("User doesn't exist");
            msg.setMessage("User does not exist");
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
    }
}