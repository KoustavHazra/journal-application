package com.example.journalApp.Controller;

import com.example.journalApp.Entity.User;
import com.example.journalApp.Repository.UserRepository;
import com.example.journalApp.Services.UserServices;
import com.example.journalApp.Services.WeatherService;
import com.example.journalApp.api_response.WeatherAPIResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @GetMapping("id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable ObjectId userId) {
        Optional<User> user = userServices.getUserById(userId);

        return user.map(each -> new ResponseEntity<>(each, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("update-user")
    public ResponseEntity<?> updateUserDetails(@RequestBody User updatedUserDetails) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User oldUserDetails = userServices.findByUsername(username);
            oldUserDetails.setUsername(updatedUserDetails.getUsername());
            oldUserDetails.setPassword(updatedUserDetails.getPassword());

            userServices.saveNewUser(oldUserDetails);
            return new ResponseEntity<>(oldUserDetails, HttpStatus.OK);
        } catch (Exception e) {
            // unable to send "user doesn't exist" to postman output when putting wrong details
            return new ResponseEntity<>("User doesn't exist.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("delete-user")
    public ResponseEntity<?> deleteUser() {
        // <?> is like a wildcard, which means we can pass any type of data.
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userRepository.deleteByUsername(authentication.getName());
            return new ResponseEntity<>("User is successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found.", HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping()
    public ResponseEntity<?> greetings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            WeatherAPIResponse weather = weatherService.getWeather("Mumbai");

            String response = "Hey, welcome " + authentication.getName() + ". ";
            if (weather != null) {
                response = response + "Today at Mumbai it feels like " + weather.getCurrent().getFeelslike() + "°.";
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found.", HttpStatus.NO_CONTENT);
        }
    }

}
