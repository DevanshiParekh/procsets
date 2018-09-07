package com.semaifour.facesix.rest;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("hello")
    public @ResponseBody String index() {
        return "Greetings from Spring Boot!";
    }

}