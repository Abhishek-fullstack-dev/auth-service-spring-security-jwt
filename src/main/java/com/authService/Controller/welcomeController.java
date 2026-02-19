package com.authService.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/welcome")
public class welcomeController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello this is me how are you ";
    }

    @GetMapping("/hey")
    public String hey(){
        return "hey";
    }


}
