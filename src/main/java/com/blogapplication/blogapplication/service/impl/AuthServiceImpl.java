package com.blogapplication.blogapplication.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blogapplication.blogapplication.entity.Role;
import com.blogapplication.blogapplication.entity.User;
import com.blogapplication.blogapplication.exception.BlogAPIException;
import com.blogapplication.blogapplication.payload.LoginDTO;
import com.blogapplication.blogapplication.payload.RegisterDTO;
import com.blogapplication.blogapplication.repository.RoleRepository;
import com.blogapplication.blogapplication.repository.UserRepository;
import com.blogapplication.blogapplication.security.JwtTokenProvider;
import com.blogapplication.blogapplication.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsernameOrEmail(),
                        loginDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public String register(RegisterDTO registerDTO) {

        // Check username already exists
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        // FIXED: was existsByUsername(email) — must use existsByEmail
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setName(registerDTO.getName());
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BlogAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_USER not found in database"));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "Registration Successful";
    }
}
