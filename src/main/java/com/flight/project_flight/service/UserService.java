package com.flight.project_flight.service;

import com.flight.project_flight.config.CustomUserDetailsService;
import com.flight.project_flight.models.UserEntity;
import com.flight.project_flight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String rawPassword) {
        String encodedPassword = customUserDetailsService.encodePassword(rawPassword);

        // Create the user entity and set the encoded password
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(encodedPassword);
        userRepository.save(userEntity);
    }
}
