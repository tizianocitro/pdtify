package it.unisa.tiziano.pdtify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping("")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok().body("<h1>Welcome to PDtify!</h1>");
    };
}
