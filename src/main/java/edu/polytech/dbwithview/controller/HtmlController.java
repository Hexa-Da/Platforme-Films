package edu.polytech.dbwithview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping(value = "/coucou")
    public String index(){return "index";}
}
