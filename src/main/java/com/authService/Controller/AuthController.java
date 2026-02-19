package com.authService.Controller;

import com.authService.Dto.ApiResponse;
import com.authService.Dto.LoginDto;
import com.authService.Dto.Userdto;
import com.authService.Service.AuthService;
import com.authService.Service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final AuthenticationManager authManager;

    public AuthController(
            AuthService authService,
            AuthenticationManager authManager,
            JwtService jwtService
    ) {
        this.authService = authService;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody Userdto dto) {
        ApiResponse<String> response = authService.register(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginCheck(@RequestBody LoginDto loginDto) {
        ApiResponse<String> response = new ApiResponse<>();

        try {
            // Authenticate user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            // Fetch role
            String role = authentication.getAuthorities()
                    .iterator().next().getAuthority();

            // Generate JWT
            String jwtToken = jwtService.generateToken(loginDto.getUsername(), role);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Login successful");
            response.setData(jwtToken);  // return token to frontend

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Invalid username or password");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Server error");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
