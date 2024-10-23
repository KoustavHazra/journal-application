package com.example.journalApp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SayHello {

    @GetMapping("/knock-knock")
    public String sayHello() {
        return "Konnichiwa !!";
    }
}
