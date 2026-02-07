package com.server.service;

import com.server.model.PasswordResetToken;
import com.server.model.User;
import com.server.repository.PasswordResetTokenRepository;
import com.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private JavaMailSender mailSender;

    @Transactional
    public Map<String, Object> requestReset(String email) {
        Optional<User> opt = userRepository.findByEmail(email);

        if (opt.isPresent()) {
            String token = UUID.randomUUID().toString();
            tokenRepository.deleteByEmail(email);
            tokenRepository.save(new PasswordResetToken(email, token, LocalDateTime.now().plusMinutes(30)));

            sendResetEmail(email, token);
        }

        return Map.of("status", "success", "message", "If the email exists, the letter will be sent.");
    }

    private void sendResetEmail(String email, String token) {
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText("To reset your password, follow the link: " + resetUrl);

        mailSender.send(message);
    }

    public Map<String, Object> confirmReset(String token, String newPassword) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);
        if (opt.isEmpty()) return Map.of("status", "error", "message", "Invalid token");

        PasswordResetToken reset = opt.get();
        if (reset.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(reset);
            return Map.of("status", "error", "message", "Token expired");
        }

        Optional<User> userOpt = userRepository.findByEmail(reset.getEmail());
        if (userOpt.isEmpty()) return Map.of("status", "error", "message", "User not found");

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(reset);
        return Map.of("status", "success", "message", "Password reset successfully");
    }
}