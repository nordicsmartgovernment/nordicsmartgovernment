package no.nsg.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Controller
@RestControllerAdvice
@CrossOrigin
public class HealthController {

    @GetMapping(value="/ping", produces={"text/plain"})
    public ResponseEntity<String> getPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value="/ready")
    public ResponseEntity<Void> getReady() {
        return ResponseEntity.ok().build();
    }

}
