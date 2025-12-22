package com. example.fms.auth.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.example.fms.auth.dto.SignupRequest;
import com.example.fms.auth.dto. ChangePasswordRequest;
import com.example.fms.auth. model.User;
import com. example.fms.auth.model.ERole;
import com. example.fms.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() == null ? ERole.ROLE_CUSTOMER : req.getRole())
                .build();

        return userRepository.save(user);
    }

    // NEW METHOD - Change Password
    public void changePassword(ChangePasswordRequest req) {
        // Find user by email
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(req. getNewPassword()));
        userRepository.save(user);
    }
}