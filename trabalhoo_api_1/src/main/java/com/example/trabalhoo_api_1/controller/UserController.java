package com.example.trabalhoo_api_1.controller;
import com.example.trabalhoo_api_1.service.UserService;
import com.example.trabalhoo_api_1.entity.User;
import com.example.trabalhoo_api_1.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "A role deve ser informada!"));
            }

            if (user.getRoles().size() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "O usuário pode ter apenas uma role!"));
            }


            String roleName = user.getRoles().iterator().next().getName();


            Role role = userService.findRoleByName(roleName);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Role inválida!"));
            }

            user.setRoles(Set.of(role));

            User createdUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Erro ao registrar usuário."));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        boolean isAuthenticated = userService.authenticate(user);

        if (isAuthenticated) {
            Set<Role> roles = user.getRoles();
            if (roles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Usuário não tem roles associadas!"));
            }

            String token = userService.generateToken(user.getUsername(), roles);
            return ResponseEntity.ok().body(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciais inválidas!"));
        }
    }
}