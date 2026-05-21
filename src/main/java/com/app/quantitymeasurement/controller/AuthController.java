package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Google OAuth2 login and JWT token endpoints")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "Initiate Google OAuth2 Login")
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Redirect to Google OAuth2 login");
        response.put("redirectUrl", "/oauth2/authorization/google");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Google OAuth2 Callback")
    @GetMapping("/callback/google")
    public ResponseEntity<Map<String, Object>> googleCallback(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");

        String token = jwtTokenProvider.generateToken(email, name, picture);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);
        response.put("name", name);
        response.put("picture", picture);

        LOGGER.info("JWT token issued for: {}", email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate JWT Token")
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            response.put("message", "No Bearer token provided");
            return ResponseEntity.status(401).body(response);
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtTokenProvider.validateToken(token);

        response.put("valid", isValid);
        if (isValid) {
            response.put("email", jwtTokenProvider.getEmailFromToken(token));
            response.put("name", jwtTokenProvider.getNameFromToken(token));
        } else {
            response.put("message", "Invalid or expired token");
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Current User Info")
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2User principal,
                                                           HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (principal != null) {
            response.put("email", principal.getAttribute("email"));
            response.put("name", principal.getAttribute("name"));
            response.put("picture", principal.getAttribute("picture"));
            response.put("authenticated", true);
        } else {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    response.put("email", jwtTokenProvider.getEmailFromToken(token));
                    response.put("name", jwtTokenProvider.getNameFromToken(token));
                    response.put("authenticated", true);
                    return ResponseEntity.ok(response);
                }
            }
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }
}