package com.example.journalApp.Controller;

import com.example.journalApp.Cache.AppCache;
import com.example.journalApp.Entity.User;
import com.example.journalApp.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private AppCache appCache;

    @GetMapping("/health-check")
    public String checkHealth() {
        return "System started running on port 8080!";
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

    @GetMapping("clear-app-cache")
    public void clearAppCache() {
        appCache.init();
    }
}
