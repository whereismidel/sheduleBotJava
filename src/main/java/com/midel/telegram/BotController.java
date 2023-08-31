package com.midel.telegram;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class BotController {
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "<center><strong>БОТ ПРАЦЮЄ</strong></center>";
    }
}
