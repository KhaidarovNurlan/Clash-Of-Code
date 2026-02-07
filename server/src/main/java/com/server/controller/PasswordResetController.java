package com.server.controller;

import com.server.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestReset(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        return ResponseEntity.ok(passwordResetService.requestReset(email));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmReset(@RequestBody Map<String, String> req) {
        String token = req.get("token");
        String newPassword = req.get("newPassword");
        return ResponseEntity.ok(passwordResetService.confirmReset(token, newPassword));
    }
}
