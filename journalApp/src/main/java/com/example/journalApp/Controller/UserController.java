package com.example.journalApp.Controller;

import com.example.journalApp.Entity.User;
import com.example.journalApp.Services.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping("all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userServices.getAllUsers();
        if (users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("add-user")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        try {
            userServices.saveNewUser(newUser);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable ObjectId userId) {
        Optional<User> user = userServices.getUserById(userId);

        return user.map(each -> new ResponseEntity<>(each, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("update-user/{username}")
    public ResponseEntity<?> updateUserDetails(@PathVariable String username, @RequestBody User updatedUserDetails) {
        try {
            User oldUserDetails = userServices.findByUsername(username);
            if (oldUserDetails != null) {
                oldUserDetails.setUsername(
                        updatedUserDetails.getUsername() != null && !updatedUserDetails.getUsername().isEmpty()
                                ? updatedUserDetails.getUsername() : oldUserDetails.getUsername()
                );

                oldUserDetails.setPassword(
                        updatedUserDetails.getPassword() != null && !updatedUserDetails.getPassword().isEmpty()
                                ? updatedUserDetails.getPassword() : oldUserDetails.getPassword()
                );
            }

            userServices.saveUser(oldUserDetails);
            return new ResponseEntity<>(oldUserDetails, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("User with username '" + username + "' doesn't exist.", HttpStatus.BAD_REQUEST);
        }
    }

//    @DeleteMapping("id/{userId}")
//    public ResponseEntity<?> deleteUser(@PathVariable ObjectId userId) {
//        // <?> is like a wildcard, which means we can pass any type of data.
//        try {
//            userServices.deleteUserById(userId);
//            return new ResponseEntity<>("Entry is deleted with journalId :: " + userId.toString(),
//                    HttpStatus.OK);
//        }
//        catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//    }

}
