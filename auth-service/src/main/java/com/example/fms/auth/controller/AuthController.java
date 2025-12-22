package com.example.fms.auth. controller;

import org.springframework. beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web. bind.annotation.*;
import lombok. RequiredArgsConstructor;
import com.example.fms. auth.dto.*;
import com. example.fms.auth.model.User;
import com.example.fms.auth.security. JwtService;
import com. example.fms.auth.service.AuthService;
import com. example.fms.auth.repository.UserRepository;
import com. example.fms.auth.security.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${cookie.same-site: Strict}")
    private String cookieSameSite;

    @PostMapping("/register")
    public ResponseEntity<? > register(@Valid @RequestBody SignupRequest req) {
        try {
            User u = authService.register(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map. of(
                            "message", "User created successfully",
                            "email", u.getEmail(),
                            "role", u. getRole().name()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails. getEmail(), userDetails.getUser().getRole());

            AuthResponse res = new AuthResponse(token, "Bearer", userDetails.getEmail(), userDetails.getUser().getRole().name());

            // SECURE HTTP-ONLY COOKIE
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    . secure(cookieSecure)  // true in production
                    .path("/")
                    .maxAge(24 * 60 * 60)  // 1 day
                    .sameSite(cookieSameSite)  // "Strict" in production
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out successfully"));
    }

    // NEW ENDPOINT - Change Password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        try {
            authService.changePassword(req);
            return ResponseEntity.ok()
                    .body(Map. of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map. of("error", "Failed to change password"));
        }
    }
}