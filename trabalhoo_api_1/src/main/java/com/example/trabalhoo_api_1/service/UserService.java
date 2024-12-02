package com.example.trabalhoo_api_1.service;

import com.example.trabalhoo_api_1.entity.Role;
import com.example.trabalhoo_api_1.entity.User;
import com.example.trabalhoo_api_1.repository.UserRepository;
import io.jsonwebtoken.security.Keys;
import com.example.trabalhoo_api_1.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User saveUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));


        Set<Role> roles;
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            roles = Set.of(findRoleByName("ROLE_ADMIN"));
            user.setRoles(roles);
        }


        return userRepository.save(user);
    }



    public boolean authenticate(User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser.isPresent()) {
            return passwordEncoder.matches(user.getPassword(), foundUser.get().getPassword());
        }

        return false;
    }


    public String generateToken(String username, Set<Role> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 10);


        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());


        List<String> rolesList = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Gera o token JWT
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", rolesList)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Role findRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleName));
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));


        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}