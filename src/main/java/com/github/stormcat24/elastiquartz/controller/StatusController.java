package com.github.stormcat24.elastiquartz.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author stormcat24
 */
@Controller
@EnableAutoConfiguration
public class StatusController {

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "OK";
    }

}
