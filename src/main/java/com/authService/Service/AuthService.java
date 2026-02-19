package com.authService.Service;

import com.authService.Dto.ApiResponse;
import com.authService.Dto.Userdto;
import com.authService.entity.UserEntity;
import com.authService.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

//alt+fn+prt sc    is used to genrate constructor
    private PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiResponse<String> register(Userdto dto) {
        ApiResponse<String> response = new ApiResponse<>();

        // Basic null/empty validation (add more as needed)
        if (dto == null || dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            response.setStatus(400);
            response.setMessage("Invalid request");
            response.setData("Username is required");
            return response;
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            response.setStatus(400);
            response.setMessage("Invalid request");
            response.setData("Email is required");
            return response;
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            response.setStatus(400);
            response.setMessage("Invalid request");
            response.setData("Password is required");
            return response;
        }

        // Check username/email uniqueness using repository instance
        if (userRepo.existsByUsername(dto.getUsername().trim())) {
            response.setStatus(409); // Conflict
            response.setMessage("User already exists");
            response.setData("Username is already taken");
            return response;
        }

        if (userRepo.existsByEmail(dto.getEmail().trim())) {
            response.setStatus(409); // Conflict
            response.setMessage("User already exists");
            response.setData("Email is already registered");
            return response;
        }

        // Map DTO -> Entity (avoid copying raw password)
        UserEntity user = new UserEntity();
        // copy other fields (but do NOT copy password directly)
        BeanUtils.copyProperties(dto, user, "password");
        user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));

        try {
            userRepo.save(user);
            response.setStatus(201);
            response.setMessage("User created successfully");
            response.setData("No error found");
            return response;
        } catch (Exception e) {
            // Log the exception in real app (logger)
            response.setStatus(500);
            response.setMessage("Failed to create user");
            response.setData("Server error: " + e.getMessage());
            return response;
        }
    }
}
