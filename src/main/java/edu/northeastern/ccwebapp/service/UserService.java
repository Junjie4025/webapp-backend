package edu.northeastern.ccwebapp.service;

import edu.northeastern.ccwebapp.Util.ResponseMessage;
import edu.northeastern.ccwebapp.pojo.User;
import edu.northeastern.ccwebapp.repository.UserRepository;
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
                    message = new ResponseEntity<>(responseMessage, HttpStatus.OK);

                } else {
                    responseMessage.setMessage("Please enter valid credentials");
                    message = new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
                }
            } else {
                responseMessage.setMessage("User does not exist");
                message = new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
            }
        } else {
            responseMessage.setMessage("User is not logged in");
            message = new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
        }
        return message;
    }

    private String validateUser(User user) {

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return "username is empty or json format is not correct";
        } else if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "password is empty or json format is not correct";
        }

        String regExpression = "^([a-zA-Z0-9_.+-])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        Pattern pattern = Pattern.compile(regExpression);
        Matcher matcher = pattern.matcher(user.getUsername());

        if (!matcher.matches()) {
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
                errorMessage.setMessage("Username already exists!");
                return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
            }
            userRepository.save(ud);
            errorMessage.setMessage("User registered successfully.");
            return new ResponseEntity<>(errorMessage, HttpStatus.OK);

        } else {
            errorMessage.setMessage(responseMessage);
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
}
