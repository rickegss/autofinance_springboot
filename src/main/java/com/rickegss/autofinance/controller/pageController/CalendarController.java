package com.rickegss.autofinance.controller.pageController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarController {
    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }
}
