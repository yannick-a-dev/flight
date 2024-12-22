package com.flight.project_flight.service;

import com.flight.project_flight.models.UserEntity;
import com.flight.project_flight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User loadUserByUsername(String username) {
        // Charger l'utilisateur de la base de données
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Retourner un objet User (implémentation de UserDetails)
        return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // Vous devez transformer les rôles en GrantedAuthority
                .collect(Collectors.toList()));
    }

    public UserEntity registerUser(UserEntity userEntity) {
        if (userRepository.findByUsername(userEntity.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }
        return userRepository.save(userEntity);
    }

    public UserEntity updateUser(UserEntity userEntity) {
        UserEntity existingUser = userRepository.findByUsername(userEntity.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userEntity.getUsername()));
        if (!existingUser.getPassword().equals(userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }

        existingUser.setPassword(userEntity.getPassword());
        existingUser.setRoles(userEntity.getRoles()); // Exemple de mise à jour des rôles
        return userRepository.save(existingUser);
    }

    public List<String> getUserRoles(String username) {
        User user = (User) loadUserByUsername(username); // Assurez-vous que 'loadUserByUsername' retourne un objet de type User
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Récupère le nom du rôle (par exemple, "ROLE_USER")
                .collect(Collectors.toList());
    }

}
