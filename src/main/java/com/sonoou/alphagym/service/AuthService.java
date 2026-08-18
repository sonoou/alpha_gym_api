package com.sonoou.alphagym.service;

import com.sonoou.alphagym.dto.AuthResponse;
import com.sonoou.alphagym.dto.LoginRequest;
import com.sonoou.alphagym.dto.SignupRequest;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.repository.UserRepository;
import com.sonoou.alphagym.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setWeight(request.getWeight());
        user.setHeight(request.getHeight());
        user.setGender(request.getGender());
        user.setFitnessGoal(request.getFitnessGoal());
        user.setRole("ROLE_USER");
        user.setOnboardingCompleted(false);

        UserEntity savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser.getEmail());

        return new AuthResponse(token, savedUser.getName(), savedUser.getEmail(), savedUser.getOnboardingCompleted(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token, user.getName(), user.getEmail(), user.getOnboardingCompleted(), user.getRole());
    }

    public AuthResponse adminLogin(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String userRole = user.getRole();
        if (userRole == null || (!userRole.equalsIgnoreCase("ROLE_ADMIN") && !userRole.equalsIgnoreCase("ADMIN"))) {
            throw new BadCredentialsException("Access Denied: Administrator privileges required for admin login.");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(token, user.getName(), user.getEmail(), user.getOnboardingCompleted(), user.getRole());
    }
}
